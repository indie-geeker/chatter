package com.indiegeeker.context.infrastructure.web.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.indiegeeker.context.domain.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 基于Header的用户信息解析器
 * 从HTTP请求的指定header中解析用户上下文信息
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Slf4j
public class HeaderUserInfoParser implements UserInfoParser, Ordered {

    private static final String DEFAULT_USER_INFO_HEADER = "X-User-Info";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    private final String headerName;

    public HeaderUserInfoParser() {
        this(DEFAULT_USER_INFO_HEADER);
    }

    public HeaderUserInfoParser(String headerName) {
        this.headerName = headerName != null ? headerName : DEFAULT_USER_INFO_HEADER;
    }

    @Override
    public UserContext parseUserContext(HttpServletRequest request) {
        if (!supports(request)) {
            return null;
        }

        String userInfoHeader = request.getHeader(headerName);
        if (userInfoHeader == null || userInfoHeader.trim().isEmpty()) {
            log.debug("请求中不包含用户信息header: {}", headerName);
            return null;
        }

        try {
            // Base64解码
            String json = new String(Base64.getDecoder().decode(userInfoHeader), StandardCharsets.UTF_8);
            
            // 反序列化为UserContext
            UserContext userContext = OBJECT_MAPPER.readValue(json, UserContext.class);
            
            log.debug("从header {} 成功解析用户信息: userId={}", headerName, userContext.getUserId());
            return userContext;
            
        } catch (Exception e) {
            log.warn("解析用户信息header失败，header: {}, 错误: {}", headerName, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean supports(HttpServletRequest request) {
        return request.getHeader(headerName) != null;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    /**
     * 获取header名称
     */
    public String getHeaderName() {
        return headerName;
    }
} 