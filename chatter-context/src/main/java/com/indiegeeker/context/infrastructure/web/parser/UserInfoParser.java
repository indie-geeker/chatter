package com.indiegeeker.context.infrastructure.web.parser;

import com.indiegeeker.context.domain.UserContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户信息解析器接口
 * 支持从HTTP请求中解析用户上下文信息的不同实现方式
 * 
 * Author: wen
 * Date: 2025/6/30
 */
public interface UserInfoParser {

    /**
     * 从HTTP请求中解析用户上下文信息
     * 
     * @param request HTTP请求
     * @return 解析出的用户上下文，如果无法解析则返回null
     */
    UserContext parseUserContext(HttpServletRequest request);

    /**
     * 获取解析器的优先级，数值越小优先级越高
     * 
     * @return 优先级
     */
    default int getOrder() {
        return 0;
    }

    /**
     * 检查此解析器是否支持当前请求
     * 
     * @param request HTTP请求
     * @return 是否支持
     */
    default boolean supports(HttpServletRequest request) {
        return true;
    }
} 