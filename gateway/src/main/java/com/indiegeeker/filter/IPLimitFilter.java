package com.indiegeeker.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.indiegeeker.BaseProperties;
import com.indiegeeker.base.BaseJSONResult;
import com.indiegeeker.enums.ResponseStatusEnum;
import com.indiegeeker.utils.GateWayIPUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Desc: IP拦截器，限制访问次数
 * Author: wen
 * Date: 2025/6/24
 **/
@Component
@Slf4j
@RefreshScope
public class IPLimitFilter extends BaseProperties implements GlobalFilter, Ordered {

    static {
        System.out.println("IPLimitFilter 类已加载");
    }

    public IPLimitFilter() {
        System.out.println("IPLimitFilter 实例已创建");
    }
    
    // 创建一个静态的ObjectMapper实例，配置好Java时间模块
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    /**
     * 需求：
     * 判断某个请求的ip在 x 秒内的请求次数是否超过 y 次
     * 如果超过 y 次，则限制访问 n 秒
     * 等待 n 秒静默后，才能够继续恢复访问
     */

//    @Value默认不支持运行期动态更新，需要结合@RefreshScope注解实现动态刷新，
//    @NacosConfig默认支持运行期动态更新。。
    @Value("${blackIp.continueCounts}")
    private Integer continueCounts;

    @Value("${blackIp.timeInterval}")
    private Integer timeInterval;

    @Value("${blackIp.limitTimes}")
    private Integer limitTimes;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("IPLimitFilter filter 方法被调用");
        System.out.println("请求路径: " + exchange.getRequest().getPath());
        System.out.println("continueCounts: " + continueCounts);
        System.out.println("timeInterval: " + timeInterval);
        log.info("IPLimitFilter filter 方法被调用, 路径: {}", exchange.getRequest().getPath());
        log.info("IPLimitFilter continueCounts={} timeInterval={}", continueCounts, timeInterval);
        return doFiltration(exchange, chain);
    }

    private Mono<Void> doFiltration(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 根据request获得请求ip
        ServerHttpRequest request = exchange.getRequest();
        String ip = GateWayIPUtils.getIP(request);
        // 正常的ip定义 - 用于计数
        final String ipRedisKey = "gateway-ip:" + ip;
        // 被拦截的黑名单ip，如果在redis中存在，则表示当前ip被限制
        final String ipRedisLimitKey = "gateway-ip:limit:" + ip;
        // 获得当前的ip并且查询还剩下多少时间，如果时间存在（大于0），则表示当前仍然处在黑名单中
        long limitExpire = redis.getExpire(ipRedisLimitKey);
        if (limitExpire > 0) {
            // 终止请求，返回错误
            return renderErrorMsg(exchange, ResponseStatusEnum.BLACK_IP);
        }
        // 获取 IP 在 redis 中的累加次数 - 应该对计数键进行递增
        Long incrementCount = redis.increment(ipRedisKey,1);
        /**
         * 判断如果是第一次进来，也就是从0开始计数，则初期访问就是1，
         * 需要设置间隔的时间，也就是连续请求的次数的间隔时间
         */
        if (incrementCount == 1) {
            redis.setExpire(ipRedisKey,timeInterval);
        }

        log.info("IP: {}, 当前计数: {}, 限制次数: {}, 剩余时间: {}", 
        ip, incrementCount, continueCounts, redis.getExpire(ipRedisKey));

        /**
         * 如果还能获得请求的正常次数，说明用户的连续请求落在限定的[timeInterval]之内
         * 一旦请求次数超过限定的连续访问次数[continueCounts]，则需要限制当前的ip
         */
        if (incrementCount > continueCounts) {
            // 限制ip访问的时间[limitTimes]
            redis.set(ipRedisLimitKey,ipRedisLimitKey,limitTimes, TimeUnit.SECONDS);
            // 终止请求，返回错误
            return renderErrorMsg(exchange, ResponseStatusEnum.BLACK_IP);
        }



        return chain.filter(exchange);
    }

    /**
     * 重新包装并且返回错误信息
     *
     * @param exchange
     * @param statusEnum
     * @return
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum statusEnum) {
        // 1. 获得相应response
        ServerHttpResponse response = exchange.getResponse();
        // 2. 设置header类型
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }
        // 3. 修改response的状态码code为500
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        // 4. 构建jsonResult
        BaseJSONResult<Object> jsonResult = BaseJSONResult.error(statusEnum);
        
        try {
            // 使用配置好的ObjectMapper进行序列化
            String json = OBJECT_MAPPER.writeValueAsString(jsonResult);
            DataBuffer dataBuffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            // 如果序列化失败，返回简单的错误信息
            String errorJson = "{\"code\": 500, \"msg\": \"系统错误\", \"timestamp\": \"" + 
                              java.time.LocalDateTime.now() + "\"}";
            DataBuffer dataBuffer = response.bufferFactory().wrap(errorJson.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }
    }

    // 过滤器的顺序，数字越小则优先级越高
    @Override
    public int getOrder() {
        return 1;
    }
}
