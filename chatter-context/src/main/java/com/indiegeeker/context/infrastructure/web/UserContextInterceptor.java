package com.indiegeeker.context.infrastructure.web;

import com.indiegeeker.context.application.UserContextHolder;
import com.indiegeeker.context.domain.UserContext;
import com.indiegeeker.context.infrastructure.web.parser.UserInfoParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

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

    private final List<UserInfoParser> userInfoParsers;

    public UserContextInterceptor(List<UserInfoParser> userInfoParsers) {
        this.userInfoParsers = userInfoParsers != null ? userInfoParsers : List.of();
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, 
                           @NonNull HttpServletResponse response, 
                           @NonNull Object handler) throws Exception {
        
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        
        log.debug("用户上下文拦截器处理请求: {} {}", method, requestPath);

        // 尝试使用各种解析器解析用户信息
        UserContext userContext = null;
        for (UserInfoParser parser : userInfoParsers) {
            userContext = parser.parseUserContext(request);
            if (userContext != null) {
                log.debug("使用解析器 {} 成功解析用户信息", parser.getClass().getSimpleName());
                break;
            }
        }
        
        if (userContext != null) {
            // 补充请求信息
            userContext.setRequestIp(getClientIp(request));
            userContext.setUserAgent(request.getHeader("User-Agent"));
            
            // 设置到ThreadLocal
            UserContextHolder.setContext(userContext);
            
            log.debug("成功设置用户上下文: userId={}, username={}", 
                     userContext.getUserId(), userContext.getUsername());
        } else {
            log.debug("请求中不包含用户信息，可能是公开接口: {}", requestPath);
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