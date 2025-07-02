package com.indiegeeker.interceptor;

import com.indiegeeker.context.UserContext;
import com.indiegeeker.context.UserContextHolder;
import com.indiegeeker.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户上下文拦截器
 * 在请求进入控制器前，从header中解析用户信息并设置到ThreadLocal
 * 在请求结束后，清理ThreadLocal，防止内存泄漏
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Slf4j
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
                           @NonNull HttpServletResponse response, 
                           @NonNull Object handler) throws Exception {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("用户上下文拦截器处理请求: {} {}", method, requestPath);

        // 从header中获取用户信息
        String userInfoHeader = request.getHeader(JWTUtils.getUserInfoHeaderName());
        
        if (userInfoHeader != null && !userInfoHeader.trim().isEmpty()) {
            // 解析用户信息
            UserContext userContext = JWTUtils.parseUserInfoHeader(userInfoHeader);
            
            if (userContext != null) {
                // 补充请求信息
                userContext.setRequestIp(getClientIp(request));
                userContext.setUserAgent(request.getHeader("User-Agent"));
                
                // 设置到ThreadLocal
                UserContextHolder.setContext(userContext);
                
                log.debug("成功设置用户上下文: userId={}, username={}", 
                         userContext.getUserId(), userContext.getUsername());
            } else {
                log.warn("解析用户信息header失败，路径: {}", requestPath);
            }
        } else {
            log.debug("请求中不包含用户信息header，可能是公开接口: {}", requestPath);
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, 
                              @NonNull HttpServletResponse response, 
                              @NonNull Object handler, 
                              Exception ex) throws Exception {
        
        // 清理ThreadLocal，防止内存泄漏
        UserContextHolder.clear();
        
        if (ex != null) {
            log.error("请求处理异常: {} {}", request.getMethod(), request.getRequestURI(), ex);
        }
    }

    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null ? remoteAddr : "unknown";
    }
} 