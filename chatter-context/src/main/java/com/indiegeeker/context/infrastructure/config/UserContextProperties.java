package com.indiegeeker.context.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 用户上下文配置属性
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Data
@ConfigurationProperties(prefix = "chatter.context")
public class UserContextProperties {

    /**
     * Web相关配置
     */
    private Web web = new Web();

    /**
     * 用户信息解析相关配置
     */
    private Parser parser = new Parser();

    @Data
    public static class Web {
        
        /**
         * 是否启用Web功能
         */
        private boolean enabled = true;

        /**
         * 拦截器排除路径
         */
        private String[] excludePathPatterns = {
                "/error/**",
                "/actuator/**", 
                "/health/**",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/doc.html"
        };
    }

    @Data
    public static class Parser {
        
        /**
         * 用户信息header名称
         */
        private String userInfoHeader = "X-User-Info";

        /**
         * 是否启用debug模式
         */
        private boolean debug = false;
    }
} 