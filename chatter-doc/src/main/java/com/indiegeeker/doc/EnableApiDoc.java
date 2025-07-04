package com.indiegeeker.doc;

import com.indiegeeker.doc.config.DocAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 Chatter 文档功能
 * <p>
 * 在主应用类上添加此注解，即可启用 knife4j 文档功能
 * <p>
 * 使用示例：
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableApiDoc
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 *
 * @author indiegeeker
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DocAutoConfiguration.class)
public @interface EnableApiDoc {
} 