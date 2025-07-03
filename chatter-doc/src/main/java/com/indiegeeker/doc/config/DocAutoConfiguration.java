package com.indiegeeker.doc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * 文档自动配置类
 *
 * @author indiegeeker
 */
@AutoConfiguration
@Configuration
@EnableConfigurationProperties(DocProperties.class)
@ConditionalOnProperty(prefix = "chatter.doc", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DocAutoConfiguration {

    private final DocProperties docProperties;

    public DocAutoConfiguration(DocProperties docProperties) {
        this.docProperties = docProperties;
    }

    /**
     * 配置 OpenAPI 基本信息
     *
     * @return OpenAPI 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        // 基本信息
        Info info = new Info()
                .title(docProperties.getTitle())
                .version(docProperties.getVersion())
                .description(docProperties.getDescription());

        // 服务条款
        if (StringUtils.hasText(docProperties.getTermsOfServiceUrl())) {
            info.termsOfService(docProperties.getTermsOfServiceUrl());
        }

        // 联系人信息
        DocProperties.Contact contactConfig = docProperties.getContact();
        if (StringUtils.hasText(contactConfig.getName()) ||
                StringUtils.hasText(contactConfig.getEmail()) ||
                StringUtils.hasText(contactConfig.getUrl())) {
            Contact contact = new Contact();
            if (StringUtils.hasText(contactConfig.getName())) {
                contact.setName(contactConfig.getName());
            }
            if (StringUtils.hasText(contactConfig.getEmail())) {
                contact.setEmail(contactConfig.getEmail());
            }
            if (StringUtils.hasText(contactConfig.getUrl())) {
                contact.setUrl(contactConfig.getUrl());
            }
            info.setContact(contact);
        }

        // 许可证信息
        DocProperties.License licenseConfig = docProperties.getLicense();
        if (StringUtils.hasText(licenseConfig.getName()) ||
                StringUtils.hasText(licenseConfig.getUrl())) {
            License license = new License();
            if (StringUtils.hasText(licenseConfig.getName())) {
                license.setName(licenseConfig.getName());
            }
            if (StringUtils.hasText(licenseConfig.getUrl())) {
                license.setUrl(licenseConfig.getUrl());
            }
            info.setLicense(license);
        }

        openAPI.setInfo(info);

        // 服务器信息
        DocProperties.Server serverConfig = docProperties.getServer();
        if (StringUtils.hasText(serverConfig.getUrl())) {
            Server server = new Server();
            server.setUrl(serverConfig.getUrl());
            if (StringUtils.hasText(serverConfig.getDescription())) {
                server.setDescription(serverConfig.getDescription());
            }
            openAPI.setServers(Collections.singletonList(server));
        }

        return openAPI;
    }

    /**
     * 配置 API 分组
     *
     * @return GroupedOpenApi 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public GroupedOpenApi defaultApi() {
        DocProperties.GroupConfig groupConfig = docProperties.getGroup();
        return GroupedOpenApi.builder()
                .group(groupConfig.getName())
                .packagesToScan(groupConfig.getBasePackage())
                .build();
    }

} 