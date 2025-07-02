package com.indiegeeker.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 认证服务 Knife4j API文档配置
 * 基于 Knife4j 4.5.0 版本，支持 OpenAPI 3.0 和 Jakarta EE
 * 
 * Author: wen
 * Date: 2025/6/30
 **/
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    /**
     * 配置认证服务 OpenAPI 文档基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chatter 认证服务 API")
                        .description("用户认证、注册、登录、短信验证码等相关接口")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("wen")
                                .email("wen@indiegeeker.com")
                                .url("https://indiegeeker.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .externalDocs(new ExternalDocumentation()
                        .description("Chatter 认证服务文档")
                        .url("https://docs.indiegeeker.com/auth"));
    }

    /**
     * 认证接口分组
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("01-认证接口")
                .displayName("用户认证相关接口")
                .pathsToMatch("/auth/**")
                .build();
    }

    /**
     * 测试接口分组
     */
    @Bean
    public GroupedOpenApi testApi() {
        return GroupedOpenApi.builder()
                .group("99-测试接口")
                .displayName("测试和调试接口")
                .pathsToMatch("/authTest/**")
                .build();
    }
} 