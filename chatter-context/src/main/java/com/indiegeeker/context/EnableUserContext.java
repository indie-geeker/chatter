package com.indiegeeker.context;

import com.indiegeeker.context.infrastructure.config.UserContextAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用用户上下文功能
 * 
 * 通过在主配置类上添加此注解，自动配置用户上下文相关功能：
 * 1. 用户上下文拦截器
 * 2. 用户信息解析器
 * 3. 相关的Web配置
 * 
 * 示例用法：
 * <pre>
 * {@code
 * @SpringBootApplication
 * @EnableUserContext
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }
 * </pre>
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(UserContextAutoConfiguration.class)
public @interface EnableUserContext {

    /**
     * 是否启用Web拦截器
     * 默认为true，在Web环境下会自动注册拦截器
     */
    boolean enableWebInterceptor() default true;

    /**
     * 用户信息header名称
     * 默认为 X-User-Info
     */
    String userInfoHeader() default "X-User-Info";

    /**
     * 拦截器排除路径
     * 这些路径不会被用户上下文拦截器处理
     */
    String[] excludePathPatterns() default {
            "/error/**",
            "/actuator/**", 
            "/health/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/doc.html"
    };
} 