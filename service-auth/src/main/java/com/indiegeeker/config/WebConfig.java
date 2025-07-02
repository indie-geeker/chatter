package com.indiegeeker.config;

import com.indiegeeker.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器、跨域等Web相关设置
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册用户上下文拦截器
        registry.addInterceptor(new UserContextInterceptor())
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        // 排除不需要用户上下文的路径
                        "/auth/getSMSCode",    // 获取验证码（公开接口）
                        "/auth/login",         // 登录（公开接口）
                        "/auth/register",      // 注册（公开接口）
                        "/authTest/**",        // 测试接口
                        "/error/**",           // 错误页面
                        "/actuator/**",        // 监控端点
                        "/doc.html",           // API文档
                        "/swagger-ui/**",      // Swagger UI
                        "/v3/api-docs/**"      // OpenAPI文档
                );
    }
} 