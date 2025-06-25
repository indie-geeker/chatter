package com.indiegeeker.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Desc: 测试过滤器
 * Author: wen
 * Date: 2025/6/24
 **/
@Component
@Slf4j
public class TestFilter implements GlobalFilter, Ordered {

    static {
        System.out.println("TestFilter 类已加载");
    }

    public TestFilter() {
        System.out.println("TestFilter 实例已创建");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("TestFilter filter 方法被调用");
        System.out.println("请求路径: " + exchange.getRequest().getPath());
        log.info("TestFilter filter 方法被调用, 路径: {}", exchange.getRequest().getPath());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0; // 优先级比 IPLimitFilter 高
    }
} 