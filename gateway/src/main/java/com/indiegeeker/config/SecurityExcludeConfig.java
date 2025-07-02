package com.indiegeeker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 安全排除路径配置
 * Author: wen
 * Date: 2025/6/30
 */
@Data
@Component
@ConfigurationProperties(prefix = "security.exclude")
public class SecurityExcludeConfig {

    /**
     * 从配置文件读取的排除路径
     */
    private List<String> paths = new ArrayList<>();

    /**
     * 获取所有排除路径（包括默认 + 配置文件）
     */
    public List<String> getAllExcludePaths() {
        List<String> allPaths = new ArrayList<>();
        
        // 添加默认排除路径
        allPaths.addAll(getDefaultExcludePaths());
        
        // 添加配置文件中的路径
        allPaths.addAll(paths);
        
        return allPaths;
    }

    /**
     * 获取默认排除路径
     */
    private List<String> getDefaultExcludePaths() {
        return Arrays.asList(
                // ========== 静态资源 ==========
                "/favicon.ico",
                "/static/**",
                "/assets/**",
                "/images/**",
                "/css/**",
                "/js/**",
                
                // ========== API 文档相关 ==========
                "/doc.html",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**",
                
                // ========== 健康检查 ==========
                "/actuator/**",
                "/health",
                "/info",
                
                // ========== 认证相关（无需鉴权）==========
                "/auth/getSMSCode",
                "/auth/login",
                "/auth/register",
                "/auth/logout",
                
                // ========== 测试接口 ==========
                "/*/hello",
                "/test/**",
                "/authTest/**",
                
                // ========== 错误页面 ==========
                "/error/**",
                "/404",
                "/500"
        );
    }
} 