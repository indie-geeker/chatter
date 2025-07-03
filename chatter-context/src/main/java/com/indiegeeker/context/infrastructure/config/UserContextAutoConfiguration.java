package com.indiegeeker.context.infrastructure.config;

import com.indiegeeker.context.infrastructure.web.UserContextInterceptor;
import com.indiegeeker.context.infrastructure.web.parser.HeaderUserInfoParser;
import com.indiegeeker.context.infrastructure.web.parser.UserInfoParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 用户上下文自动配置类
 * 
 * 根据@EnableUserContext注解自动配置用户上下文相关组件：
 * 1. 用户信息解析器
 * 2. 用户上下文拦截器
 * 3. Web MVC配置
 * 
 * Author: wen
 * Date: 2025/6/30
 */
@Slf4j
@Configuration
@ConditionalOnClass(HandlerInterceptor.class)
@EnableConfigurationProperties(UserContextProperties.class)
public class UserContextAutoConfiguration {

    private final UserContextProperties properties;

    public UserContextAutoConfiguration(UserContextProperties properties) {
        this.properties = properties;
    }

    /**
     * 默认的Header用户信息解析器
     */
    @Bean
    @ConditionalOnMissingBean(HeaderUserInfoParser.class)
    public HeaderUserInfoParser headerUserInfoParser() {
        String headerName = properties.getParser().getUserInfoHeader();
        
        log.info("创建Header用户信息解析器，header名称: {}", headerName);
        return new HeaderUserInfoParser(headerName);
    }

    /**
     * 用户上下文拦截器
     */
    @Bean
    @ConditionalOnMissingBean(UserContextInterceptor.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public UserContextInterceptor userContextInterceptor(@Autowired(required = false) List<UserInfoParser> userInfoParsers) {
        log.info("创建用户上下文拦截器，解析器数量: {}", userInfoParsers != null ? userInfoParsers.size() : 0);
        return new UserContextInterceptor(userInfoParsers);
    }

    /**
     * Web MVC配置 - 注册拦截器
     */
    @Configuration
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnProperty(name = "chatter.context.web.enabled", havingValue = "true", matchIfMissing = true)
    public static class UserContextWebMvcConfiguration implements WebMvcConfigurer {

        private final UserContextInterceptor userContextInterceptor;
        private final UserContextProperties properties;

        public UserContextWebMvcConfiguration(UserContextInterceptor userContextInterceptor, 
                                             UserContextProperties properties) {
            this.userContextInterceptor = userContextInterceptor;
            this.properties = properties;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            String[] excludePatterns = properties.getWeb().getExcludePathPatterns();

            log.info("注册用户上下文拦截器，排除路径: {}", String.join(", ", excludePatterns));
            
            registry.addInterceptor(userContextInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(excludePatterns);
        }
    }


} 