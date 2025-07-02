package com.indiegeeker.filter;

import com.indiegeeker.config.SecurityExcludeConfig;
import com.indiegeeker.context.UserContext;
import com.indiegeeker.utils.JWTUtils;
import com.indiegeeker.utils.PathMatcherUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 网关安全过滤器
 * 负责JWT token解析、用户认证和上下文传递
 * 
 * Author: wen
 * Date: 2025/7/2
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter implements GlobalFilter, Ordered {

    private final SecurityExcludeConfig securityExcludeConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求信息
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();
        String method = request.getMethod().name();
        String clientIp = getClientIp(request);
        
        log.debug("安全过滤器处理请求: {} {} from {}", method, requestPath, clientIp);

        // 检查是否需要排除路径鉴权
        if (isExcludedPath(requestPath)) {
            log.debug("路径 {} 在排除列表中，跳过鉴权", requestPath);
            return chain.filter(exchange);
        }

        // 获取Authorization header
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求 {} 缺少有效的Authorization header", requestPath);
            return handleUnauthorized(exchange);
        }

        // 解析JWT token
        String token = authHeader.substring(7);
        UserContext userContext = JWTUtils.parseToken(token);
        
        if (userContext == null) {
            log.warn("请求 {} JWT token解析失败", requestPath);
            return handleUnauthorized(exchange);
        }

        if (userContext.isExpired()) {
            log.warn("请求 {} JWT token已过期", requestPath);
            return handleUnauthorized(exchange);
        }

        // 补充请求信息到用户上下文
        userContext.setRequestIp(clientIp);
        userContext.setUserAgent(request.getHeaders().getFirst(HttpHeaders.USER_AGENT));

        // 检查权限
        if (!hasPermission(userContext, requestPath, method)) {
            log.warn("用户 {} 对路径 {} 权限不足", userContext.getUserId(), requestPath);
            return handleForbidden(exchange);
        }

        log.debug("用户 {} 请求 {} 鉴权通过", userContext.getUserId(), requestPath);

        // 将用户信息添加到请求header中，传递给下游微服务
        ServerHttpRequest modifiedRequest = addUserContextToRequest(request, userContext);
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

        return chain.filter(modifiedExchange);
    }

    /**
     * 检查路径是否在排除列表中
     */
    private boolean isExcludedPath(String requestPath) {
        List<String> excludePaths = securityExcludeConfig.getAllExcludePaths();
        boolean isExcluded = PathMatcherUtils.isExcluded(requestPath, excludePaths);
        
        if (isExcluded) {
            log.debug("路径 {} 匹配排除模式", requestPath);
        }
        
        return isExcluded;
    }

    /**
     * 检查是否有权限访问
     */
    private boolean hasPermission(UserContext userContext, String requestPath, String method) {
        // TODO: 实现具体的权限检查逻辑
        // 这里可以根据用户角色、权限和请求路径进行细粒度的权限控制
        
        // 示例权限检查逻辑
        if (requestPath.startsWith("/admin/") && !userContext.hasRole("ADMIN")) {
            return false;
        }
        
        if (requestPath.startsWith("/user/") && !userContext.hasAnyRole("USER", "ADMIN")) {
            return false;
        }
        
        // 示例：DELETE操作需要特殊权限
        if ("DELETE".equals(method) && !userContext.hasPermission("DELETE")) {
            return false;
        }
        
        // 默认通过（已认证用户都有基本权限）
        return true;
    }

    /**
     * 将用户上下文信息添加到请求header中
     */
    private ServerHttpRequest addUserContextToRequest(ServerHttpRequest request, UserContext userContext) {
        // 生成用户信息header值
        String userInfoHeader = JWTUtils.generateUserInfoHeader(userContext);
        
        if (userInfoHeader == null) {
            log.error("生成用户信息header失败，用户ID: {}", userContext.getUserId());
            return request;
        }

        // 添加用户信息到请求header
        return request.mutate()
                .header(JWTUtils.getUserInfoHeaderName(), userInfoHeader)
                .header("X-User-Id", userContext.getUserId())
                .header("X-Username", userContext.getUsername())
                .header("X-Tenant-Id", userContext.getTenantId() != null ? userContext.getTenantId() : "")
                .build();
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    /**
     * 处理未认证请求
     */
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String errorResponse = """
                {
                    "code": 401,
                    "msg": "未授权访问，请先登录",
                    "data": null,
                    "timestamp": "%s"
                }
                """.formatted(java.time.LocalDateTime.now());
        
        org.springframework.core.io.buffer.DataBuffer buffer = 
                exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * 处理权限不足请求
     */
    private Mono<Void> handleForbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String errorResponse = """
                {
                    "code": 403,
                    "msg": "权限不足，无法访问该资源",
                    "data": null,
                    "timestamp": "%s"
                }
                """.formatted(java.time.LocalDateTime.now());
        
        org.springframework.core.io.buffer.DataBuffer buffer = 
                exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 设置较高优先级，确保在路由之前进行安全检查
        return -100;
    }
}
