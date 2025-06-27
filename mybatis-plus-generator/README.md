# MyBatis Plus 代码生成器

这是一个基于 MyBatis Plus 3.5.12 版本的通用代码生成器，支持自动生成 Entity、Mapper、Service、Controller 等代码文件。

## 功能特性

- 🚀 **支持多种数据库**：MySQL、PostgreSQL、Oracle、SQLServer等
- 📦 **全代码生成**：自动生成 Entity、Mapper、Service、Controller
- ⚙️ **灵活配置**：支持配置文件和代码两种配置方式
- 🎯 **智能策略**：支持字段填充、逻辑删除、命名策略等
- 📝 **注解支持**：支持 Lombok、Swagger 注解
- 🔧 **可扩展**：易于扩展和定制

## 项目结构

```
mybatis-plus-generator/
├── src/main/java/com/indiegeeker/
│   ├── MybatisPlusCodeGenerator.java        # 基础代码生成器
│   └── ConfigurableCodeGenerator.java       # 可配置代码生成器
├── src/main/resources/
│   └── generator.properties                 # 配置文件
└── README.md                                # 使用说明
```

## 快速开始

### 方式一：使用配置文件（推荐）

1. **修改配置文件**

编辑 `src/main/resources/generator.properties` 文件：

```properties
# 数据库配置
database.url=jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false
database.username=root
database.password=your_password
database.schema=your_database

# 需要生成的表名（多个用逗号分隔，留空生成所有表）
tables.include=user,role,permission

# 基础包名
generator.basePackage=com.indiegeeker
```

2. **运行代码生成器**

```bash
java -cp target/classes com.indiegeeker.ConfigurableCodeGenerator
```

或者在 IDE 中直接运行 `ConfigurableCodeGenerator.main()` 方法。

### 方式二：使用交互式生成器

运行 `MybatisPlusCodeGenerator.main()` 方法，根据提示输入表名。

### 方式三：使用快速生成器

对于简单的代码生成需求，可以直接使用 `FastAutoGenerator`：

```java
FastAutoGenerator.create("数据库URL", "用户名", "密码")
    .globalConfig(builder -> {
        builder.author("作者名")
                .outputDir("输出目录")
                .enableSwagger();
    })
    .packageConfig(builder -> {
        builder.parent("包名");
    })
    .strategyConfig(builder -> {
        builder.addInclude("表名1", "表名2")
                .entityBuilder()
                .enableLombok();
    })
    .execute();
```

## 配置说明

### 数据库配置

```properties
# MySQL 配置
database.url=jdbc:mysql://localhost:3306/database_name?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false
database.username=root
database.password=password
database.driver=com.mysql.cj.jdbc.Driver

# PostgreSQL 配置
database.url=jdbc:postgresql://localhost:5432/database_name
database.username=postgres
database.password=password
database.driver=org.postgresql.Driver

# Oracle 配置
database.url=jdbc:oracle:thin:@localhost:1521:database_name
database.username=username
database.password=password
database.driver=oracle.jdbc.OracleDriver
```

### 包配置

```properties
# 基础包名
generator.basePackage=com.indiegeeker

# 各层包名配置
package.entity=pojo.entity      # 实体类包名
package.mapper=mapper           # Mapper 包名
package.service=service         # Service 包名
package.serviceImpl=service.impl # ServiceImpl 包名
package.controller=controller   # Controller 包名
package.xml=mapper.xml          # XML 文件包名
```

### 表配置

```properties
# 需要生成的表名（多个用逗号分隔）
tables.include=user,role,permission

# 需要过滤的表前缀（多个用逗号分隔）
tables.prefix=t_,sys_
```

### 策略配置

```properties
# 启用 Lombok 注解
strategy.enableLombok=true

# 启用 Swagger 注解
strategy.enableSwagger=true

# 启用链式模型
strategy.enableChainModel=true

# 启用字段注解
strategy.enableTableFieldAnnotation=true

# 启用 @Mapper 注解
strategy.enableMapperAnnotation=true

# 启用 BaseResultMap
strategy.enableBaseResultMap=true

# 启用 BaseColumnList
strategy.enableBaseColumnList=true

# 启用 REST 风格控制器
strategy.enableRestStyle=true

# 逻辑删除字段名
strategy.logicDeleteColumnName=deleted
```

### 字段填充配置

```properties
# 创建时间字段（多个用逗号分隔）
fill.createTimeFields=create_time,created_time,gmt_create

# 更新时间字段（多个用逗号分隔）
fill.updateTimeFields=update_time,updated_time,gmt_modified
```

### 文件命名配置

```properties
# 文件名格式化（%s 代表表名）
naming.entity=%s                # User.java
naming.mapper=%sMapper          # UserMapper.java
naming.service=%sService        # UserService.java
naming.serviceImpl=%sServiceImpl # UserServiceImpl.java
naming.controller=%sController  # UserController.java
naming.xml=%sMapper             # UserMapper.xml
```

## 生成的代码示例

### Entity 示例

```java
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@ApiModel(value="User对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
```

### Mapper 示例

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

### Service 示例

```java
public interface UserService extends IService<User> {
}

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
```

### Controller 示例

```java
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    // 自动生成基础 CRUD 接口
}
```

## 支持的数据库

| 数据库 | 驱动类 | URL 示例 |
|--------|--------|----------|
| MySQL | `com.mysql.cj.jdbc.Driver` | `jdbc:mysql://localhost:3306/db` |
| PostgreSQL | `org.postgresql.Driver` | `jdbc:postgresql://localhost:5432/db` |
| Oracle | `oracle.jdbc.OracleDriver` | `jdbc:oracle:thin:@localhost:1521:db` |
| SQLServer | `com.microsoft.sqlserver.jdbc.SQLServerDriver` | `jdbc:sqlserver://localhost:1433;databaseName=db` |
| H2 | `org.h2.Driver` | `jdbc:h2:mem:db` |

## 最佳实践

1. **数据库表设计规范**
   - 使用下划线命名法（如：`user_info`）
   - 主键建议使用 `id`
   - 时间字段建议使用 `create_time`、`update_time`
   - 逻辑删除字段建议使用 `deleted`

2. **配置文件管理**
   - 敏感信息（如数据库密码）可通过环境变量注入
   - 不同环境使用不同的配置文件

3. **代码生成策略**
   - 首次生成时建议生成所有文件
   - 后续迭代时可选择性生成特定层的代码
   - 自定义代码放在生成代码的子类中，避免重复生成时被覆盖

## 注意事项

- 确保数据库连接配置正确
- 生成前备份已有代码，避免覆盖自定义修改
- 生成的代码需要相应的依赖包支持（如 Lombok、Swagger）
- 建议在独立模块中运行代码生成器

## 依赖要求

确保项目中包含以下依赖：

```xml
<!-- MyBatis Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.12</version>
</dependency>

<!-- MyBatis Plus 代码生成器 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.12</version>
</dependency>

<!-- 模板引擎 Velocity（默认） -->
<dependency>
    <groupId>org.apache.velocity</groupId>
    <artifactId>velocity-engine-core</artifactId>
    <version>2.4.1</version>
</dependency>

<!-- 数据库驱动 MySQL -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.3.0</version>
</dependency>

<!-- 其他数据库驱动（按需选择） -->
<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>

<!-- Oracle -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>23.3.0.23.09</version>
</dependency>
```

## 版本说明

当前代码生成器适用于 **MyBatis Plus 3.5.12** 版本。

### API 变化说明

- 从 MyBatis Plus 3.5.1 版本开始，官方推荐使用 `FastAutoGenerator` 替代 `AutoGenerator`
- `TableFill` 类已被 `Column` 类替代用于字段填充配置
- 配置方式更加简洁，使用链式调用和 Lambda 表达式

## 常见问题

### 1. 编译错误

**问题**: `'AutoGenerator()' has private access`
**解决**: 使用 `FastAutoGenerator` 替代 `AutoGenerator`

**问题**: `Cannot resolve symbol 'TableFill'`
**解决**: 使用 `Column` 类替代 `TableFill` 类

### 2. 运行时错误

**问题**: 连接数据库失败
**解决**: 
- 检查数据库 URL、用户名、密码
- 确认数据库服务已启动
- 检查网络连接和防火墙设置

**问题**: 生成代码为空
**解决**: 
- 检查表名配置是否正确
- 确认数据库中存在对应表
- 检查表前缀过滤配置

### 3. 编码问题

**问题**: 生成的代码出现乱码
**解决**: 
- 确保项目编码设置为 UTF-8
- 检查数据库连接URL中的编码参数
- 在IDEA中设置文件编码为 UTF-8

### 4. 依赖问题

**问题**: 模板引擎相关错误
**解决**: 
- 确保添加了 `velocity-engine-core` 依赖
- 版本冲突时可尝试排除传递依赖

## 升级指南

如果你正在从旧版本升级，请注意以下变化：

1. **构造器变化**: 使用 `FastAutoGenerator.create()` 替代 `new AutoGenerator()`
2. **配置方式**: 使用 Lambda 表达式进行配置
3. **字段填充**: 使用 `Column` 替代 `TableFill`
4. **包导入**: 更新相关的 import 语句 