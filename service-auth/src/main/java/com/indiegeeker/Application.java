package com.indiegeeker;

import com.indiegeeker.doc.EnableApiDoc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务启动类
 * Author: wen
 * Date: 2025/6/22
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableApiDoc
@MapperScan("com.indiegeeker.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
