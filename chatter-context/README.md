# Chatter Context 用户上下文管理模块

## 📋 模块概述

`chatter-context` 模块是 Chatter 微服务架构中的**应用层横切关注点模块**，专门负责用户上下文信息的管理、传递和存储。该模块提供了完整的用户上下文解决方案，包括：

- 📦 **用户上下文数据模型**：定义用户信息载体
- 🔧 **ThreadLocal 管理**：线程安全的用户上下文存储
- 🌐 **Web 拦截器**：自动从HTTP请求中解析用户信息
- 🔌 **可插拔解析器**：支持多种用户信息解析方式
- ⚙️ **自动配置**：一键启用所有功能

## 🏗️ 架构设计

### 分层结构

```
chatter-context/
├── domain/                    # 领域层
│   └── UserContext.java      # 用户上下文数据模型
├── application/               # 应用层
│   └── UserContextHolder.java # ThreadLocal管理服务
├── infrastructure/            # 基础设施层
│   ├── web/
│   │   ├── UserContextInterceptor.java      # Web拦截器
│   │   └── parser/
│   │       ├── UserInfoParser.java          # 解析器接口
│   │       └── HeaderUserInfoParser.java    # Header解析器实现
│   └── config/
│       ├── UserContextAutoConfiguration.java # 自动配置
│       └── UserContextProperties.java        # 配置属性
├── EnableUserContext.java    # 启用注解
└── UserContextUtils.java     # 工具类API
```

### 核心组件

#### 1. UserContext - 用户上下文数据模型
```java
@Data
@Builder
public class UserContext implements Serializable {
    private String userId;          // 用户ID
    private String username;        // 用户名
    private String nickname;        // 昵称
    private List<String> roles;     // 角色列表
    private Set<String> permissions; // 权限集合
    private String tenantId;        // 租户ID
    private String clientType;      // 客户端类型
    private String requestIp;       // 请求IP
    private LocalDateTime expiresAt; // 过期时间
    // ... 其他字段和方法
}
```

#### 2. UserContextHolder - ThreadLocal管理
```java
public class UserContextHolder {
    public static void setContext(UserContext userContext);
    public static UserContext getContext();
    public static String getCurrentUserId();
    public static String getCurrentUsername();
    public static boolean hasRole(String role);
    public static boolean hasPermission(String permission);
    public static void clear();
    // ... 更多便捷方法
}
```

#### 3. 可插拔解析器架构
```java
public interface UserInfoParser {
    UserContext parseUserContext(HttpServletRequest request);
    default int getOrder() { return 0; }
    default boolean supports(HttpServletRequest request) { return true; }
}
```

## 🚀 快速开始

### 1. 添加依赖

在业务服务的 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.indiegeeker</groupId>
    <artifactId>chatter-context</artifactId>
</dependency>
```

### 2. 启用功能

在主配置类上添加 `@EnableUserContext` 注解：

```java
@SpringBootApplication
@EnableUserContext
public class ServiceAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAuthApplication.class, args);
    }
}
```

### 3. 使用API

在业务代码中使用用户上下文：

```java
@RestController
public class UserController {
    
    @GetMapping("/profile")
    public UserInfo getProfile() {
        // 方式1：使用工具类
        String userId = UserContextUtils.getCurrentUserId();
        String username = UserContextUtils.getCurrentUsername();
        
        // 方式2：直接使用Holder
        UserContext context = UserContextHolder.getContext();
        
        // 方式3：权限检查
        if (UserContextUtils.hasRole("ADMIN")) {
            // 管理员逻辑
        }
        
        return userService.getProfile(userId);
    }
}
```

## ⚙️ 配置选项

在 `application.yml` 中进行配置：

```yaml
chatter:
  context:
    web:
      enabled: true  # 是否启用Web功能
      exclude-path-patterns:  # 排除路径
        - "/error/**"
        - "/actuator/**"
        - "/health/**"
        - "/swagger-ui/**"
        - "/v3/api-docs/**"
        - "/doc.html"
    parser:
      user-info-header: "X-User-Info"  # 用户信息header名称
      debug: false  # 是否启用调试模式
```

## 🔌 扩展功能

### 自定义用户信息解析器

```java
@Component
public class JwtUserInfoParser implements UserInfoParser {
    
    @Override
    public UserContext parseUserContext(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // 解析JWT token
            return parseJwtToken(token);
        }
        return null;
    }
    
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
```

### 异步处理中的用户上下文

```java
@Service
public class AsyncService {
    
    @Async
    public void processAsync() {
        // 复制当前用户上下文到异步线程
        UserContext userContext = UserContextUtils.copyContext();
        
        if (userContext != null) {
            UserContextUtils.executeWithContext(userContext, () -> {
                // 在新线程中使用用户上下文
                String userId = UserContextUtils.getCurrentUserId();
                // 业务逻辑...
            });
        }
    }
}
```

## 🎯 设计原则

### 1. 职责分离
- **领域层**：纯粹的数据模型，无依赖
- **应用层**：业务逻辑和ThreadLocal管理
- **基础设施层**：Web组件和自动配置

### 2. 可插拔架构
- 支持多种用户信息解析方式
- 可以轻松扩展新的解析器
- 按优先级顺序尝试解析

### 3. 自动配置
- 零配置即可使用
- 支持条件化装配
- 可通过配置文件定制

### 4. 类型安全
- 强类型的用户上下文模型
- 编译时检查
- IDE友好的API设计

## 🔄 微服务间传递

在微服务架构中，用户上下文通过HTTP Header在服务间传递：

1. **网关层**：解析JWT token，将用户信息写入 `X-User-Info` header
2. **下游服务**：通过拦截器自动解析header，设置到ThreadLocal
3. **业务代码**：透明使用，无需关心传递细节

## 📊 性能特性

- ✅ **轻量级**：最小化内存占用
- ✅ **无锁设计**：使用ThreadLocal避免同步开销
- ✅ **懒加载**：按需创建用户上下文
- ✅ **自动清理**：请求结束后自动清理ThreadLocal

## 🛡️ 最佳实践

1. **及时清理**：拦截器会自动清理ThreadLocal，避免内存泄漏
2. **异步处理**：使用 `copyContext()` 和 `executeWithContext()` 处理异步场景
3. **权限检查**：使用内置的角色和权限检查方法
4. **可空检查**：始终检查用户上下文是否为null
5. **配置排除**：合理配置排除路径，避免不必要的解析

---

**Author**: wen  
**Date**: 2025/6/30  
**Version**: 1.0.0 