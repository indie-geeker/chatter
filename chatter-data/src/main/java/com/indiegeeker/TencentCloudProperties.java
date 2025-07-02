package com.indiegeeker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Desc:
 * Author: wen
 * Date: 2025/6/25
 **/
@Component
@Data
@PropertySource("classpath:tencent-cloud.properties")
@ConfigurationProperties(prefix = "tencent.cloud")
public class TencentCloudProperties {
    private String SecretId;
    private String SecretKey;
    private String SdkAppId;
    private String SignName;
    private String TemplateId;
}
