# Chatter Doc 模块

Chatter Doc 模块是一个集中管理 Knife4j（OpenAPI/Swagger）文档的组件，提供统一的 API 文档配置和管理功能。

## 功能特性

- 🚀 **开箱即用**：基于 Spring Boot 自动配置，无需复杂配置
- 🎨 **统一样式**：提供统一的 API 响应格式和文档样式
- 🔧 **灵活配置**：支持通过配置文件自定义文档信息
- 📝 **注解支持**：提供常用的响应注解，简化文档编写
- 🌐 **多分组支持**：支持按模块或功能进行 API 分组管理

## 快速开始

### 1. 添加依赖

在需要使用文档功能的模块的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.indiegeeker</groupId>
    <artifactId>chatter-doc</artifactId>
</dependency>
```

### 2. 启用文档功能

#### 方式一：使用注解（推荐）

在 Spring Boot 主启动类上添加 `@EnableApiDoc` 注解：

```java
@SpringBootApplication
@EnableApiDoc
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### 方式二：自动配置

如果添加了依赖，文档功能会自动启用（推荐使用注解方式以提高可读性）。

### 3. 配置文档信息

在 `application.yml` 中添加配置：

```yaml
chatter:
  doc:
    enabled: true
    title: "您的项目 API 文档"
    version: "1.0.0"
    description: "项目 API 接口文档"
    contact:
      name: "开发团队"
      email: "dev@company.com"
    group:
      base-package: "com.yourcompany.controller"
```

### 4. 访问文档

启动应用后，访问以下地址查看文档：

- Knife4j UI：`http://localhost:8080/doc.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

## 配置说明

### 完整配置示例

```yaml
chatter:
  doc:
    # 是否启用文档
    enabled: true
    
    # 文档基本信息
    title: "Chatter API 文档"
    version: "1.0.0"
    description: "Chatter 项目 API 接口文档"
    terms-of-service-url: "https://example.com/terms"
    
    # 联系人信息
    contact:
      name: "开发团队"
      email: "dev@example.com"
      url: "https://example.com"
    
    # 许可证信息
    license:
      name: "Apache 2.0"
      url: "https://www.apache.org/licenses/LICENSE-2.0"
    
    # 服务器信息
    server:
      url: "http://localhost:8080"
      description: "本地开发环境"
    
    # 分组配置
    group:
      name: "default"
      base-package: "com.yourcompany"
      description: "默认分组"

# Knife4j 增强配置
knife4j:
  enable: true
  setting:
    language: zh_cn
    enable-search: true
    enable-footer-custom: true
    footer-custom-content: "Copyright © 2024 YourCompany"
```

### 配置项说明

| 配置项 | 说明 | 默认值 |
|-------|-----|--------|
| `chatter.doc.enabled` | 是否启用文档功能 | `true` |
| `chatter.doc.title` | 文档标题 | `"Chatter API 文档"` |
| `chatter.doc.version` | 文档版本 | `"1.0.0"` |
| `chatter.doc.description` | 文档描述 | `"Chatter 项目 API 接口文档"` |
| `chatter.doc.group.base-package` | 扫描的包路径 | `"com.indiegeeker"` |

## 使用指南

### 1. 控制器注解

在控制器类和方法上使用 OpenAPI 注解：

```java
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关操作接口")
public class UserController {
    
    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @Parameter(name = "id", description = "用户ID", required = true)
    @ApiResponseWrapper.Success("获取成功")
    public BaseJSONResult<User> getUser(@PathVariable Long id) {
        // 实现逻辑
    }
    
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    @ApiResponseWrapper.Create("创建成功")
    public BaseJSONResult<User> createUser(@RequestBody @Valid CreateUserRequest request) {
        // 实现逻辑
    }
}
```

### 2. 使用响应注解

模块提供了常用的响应注解：

```java
@ApiResponseWrapper.Success("操作成功")           // 成功响应
@ApiResponseWrapper.Create("创建成功")            // 创建响应
@ApiResponseWrapper.Update("更新成功")            // 更新响应
@ApiResponseWrapper.Delete("删除成功")            // 删除响应
@ApiResponseWrapper.Page("查询成功")              // 分页响应
@ApiResponseWrapper.Common                        // 通用响应（包含多种状态码）
```

### 3. 实体类注解

在实体类上添加描述信息：

```java
@Schema(description = "用户信息")
public class User {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "admin")
    private String username;
    
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;
    
    // getter/setter...
}
```

### 4. 自定义分组

可以通过配置创建自定义分组：

```java
@Configuration
public class CustomDocConfig {
    
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户模块")
                .packagesToScan("com.yourcompany.user.controller")
                .pathsToMatch("/api/users/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("订单模块")
                .packagesToScan("com.yourcompany.order.controller")
                .pathsToMatch("/api/orders/**")
                .build();
    }
}
```

## 最佳实践

### 1. 文档编写规范

- 为每个控制器类添加 `@Tag` 注解，描述模块功能
- 为每个接口方法添加 `@Operation` 注解，说明接口用途
- 为请求参数添加 `@Parameter` 注解，说明参数含义
- 为实体类字段添加 `@Schema` 注解，提供示例值

### 2. 响应格式统一

- 统一使用 `BaseJSONResult` 作为响应格式
- 使用提供的响应注解简化文档编写
- 为不同类型的操作使用相应的响应注解

### 3. 分组管理

- 按业务模块对 API 进行分组
- 使用有意义的分组名称
- 避免过度细分，保持分组的实用性

## 故障排除

### 1. 文档页面无法访问

- 确认 `chatter.doc.enabled` 配置为 `true`
- 检查 `knife4j.enable` 配置为 `true`
- 确认应用端口和路径配置正确

### 2. API 接口不显示

- 检查控制器包路径是否在 `base-package` 配置范围内
- 确认控制器类上有 `@RestController` 或 `@Controller` 注解
- 检查是否添加了正确的请求映射注解

### 3. 配置不生效

- 确认配置文件格式正确（YAML 缩进敏感）
- 检查配置项名称是否正确
- 重启应用使配置生效

## 版本兼容性

- Spring Boot 3.2+
- Java 17+
- Knife4j 4.5.0

## 扩展开发

如需自定义文档配置，可以：

1. 继承 `DocAutoConfiguration` 类
2. 实现自定义的 `OpenAPI` Bean
3. 创建自定义的 `GroupedOpenApi` Bean

```java
@Configuration
public class CustomDocConfiguration extends DocAutoConfiguration {
    
    public CustomDocConfiguration(DocProperties docProperties) {
        super(docProperties);
    }
    
    @Bean
    @Primary
    public OpenAPI customOpenAPI() {
        // 自定义实现
        return super.customOpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
``` 