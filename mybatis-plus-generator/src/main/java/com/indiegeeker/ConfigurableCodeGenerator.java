package com.indiegeeker;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 可配置的 MyBatis Plus 代码生成器
 * 支持从 generator.properties 配置文件读取配置
 * 
 * 使用方法：
 * 1. 修改 resources/generator.properties 配置文件
 * 2. 运行 main 方法
 * 
 * @author wen
 * @date 2025/6/26
 **/
public class ConfigurableCodeGenerator {

    private static Properties properties = new Properties();

    static {
        loadConfig();
    }

    public static void main(String[] args) {
        try {
            generateCode();
            System.out.println("代码生成完成！");
        } catch (Exception e) {
            System.err.println("代码生成失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成代码
     */
    public static void generateCode() {
        // 获取配置信息
        String url = getProperty("database.url");
        String username = getProperty("database.username");
        String password = getProperty("database.password");
        String outputDir = System.getProperty("user.dir") + getProperty("generator.outputDir");
        String author = getProperty("generator.author");
        String basePackage = getProperty("generator.basePackage");
        
        // 获取需要生成的表名
        List<String> tableNames = getIncludeTables();
        List<String> tablePrefixes = getTablePrefixes();
        
        // 使用 FastAutoGenerator 快速生成代码
        FastAutoGenerator.create(url, username, password)
                // 全局配置
                .globalConfig(builder -> {
                    builder.outputDir(outputDir)  // 输出目录
                            .author(author)  // 作者名
                            .dateType(DateType.TIME_PACK)  // 时间策略
                            .commentDate(getProperty("config.commentDateFormat", "yyyy-MM-dd"));  // 注释日期格式
                    
                    // 开启 Swagger 注解
                    if (getBooleanProperty("strategy.enableSwagger", true)) {
                        builder.enableSwagger();
                    }
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(basePackage)  // 父包名
                            .entity(getProperty("package.entity"))  // Entity 包名
                            .mapper(getProperty("package.mapper"))  // Mapper 包名
                            .service(getProperty("package.service"))  // Service 包名
                            .serviceImpl(getProperty("package.serviceImpl"))  // ServiceImpl 包名
                            .controller(getProperty("package.controller"));  // Controller 包名
                })
                // 策略配置
                .strategyConfig(builder -> {
                    // 设置需要生成的表
                    if (!tableNames.isEmpty()) {
                        builder.addInclude(tableNames);
                    }
                    
                    // 设置表前缀过滤
                    if (!tablePrefixes.isEmpty()) {
                        builder.addTablePrefix(tablePrefixes.toArray(new String[0]));
                    }
                    
                    // Entity 策略配置
                    builder.entityBuilder()
                            .naming(NamingStrategy.underline_to_camel)  // 表名生成策略
                            .columnNaming(NamingStrategy.underline_to_camel)  // 字段生成策略
                            .idType(IdType.ASSIGN_ID);  // 主键策略

                    // 根据配置启用功能
                    if (getBooleanProperty("strategy.enableLombok", true)) {
                        builder.entityBuilder().enableLombok();
                    }
                    if (getBooleanProperty("strategy.enableTableFieldAnnotation", true)) {
                        builder.entityBuilder().enableTableFieldAnnotation();
                    }
                    if (getBooleanProperty("strategy.enableChainModel", true)) {
                        builder.entityBuilder().enableChainModel();
                    }

                    // 逻辑删除字段
                    String logicDeleteColumn = getProperty("strategy.logicDeleteColumnName", "");
                    if (!logicDeleteColumn.isEmpty()) {
                        builder.entityBuilder().logicDeleteColumnName(logicDeleteColumn);
                    }

                    // 字段填充配置
                    List<Column> tableFills = getTableFills();
                    if (!tableFills.isEmpty()) {
                        builder.entityBuilder().addTableFills(tableFills.toArray(new IFill[0]));
                    }

                    // Mapper 策略配置
                    if (getBooleanProperty("strategy.enableMapperAnnotation", true)) {
                        builder.mapperBuilder().enableMapperAnnotation();
                    }
                    if (getBooleanProperty("strategy.enableBaseResultMap", true)) {
                        builder.mapperBuilder().enableBaseResultMap();
                    }
                    if (getBooleanProperty("strategy.enableBaseColumnList", true)) {
                        builder.mapperBuilder().enableBaseColumnList();
                    }

                    // Controller 策略配置
                    if (getBooleanProperty("strategy.enableRestStyle", true)) {
                        builder.controllerBuilder().enableRestStyle();
                    }
                })
                // 模板引擎配置
                .templateEngine(new VelocityTemplateEngine())
                // 执行生成
                .execute();
    }

    /**
     * 加载配置文件
     */
    private static void loadConfig() {
        try (InputStream inputStream = ConfigurableCodeGenerator.class
                .getClassLoader().getResourceAsStream("generator.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                System.out.println("成功加载配置文件：generator.properties");
            } else {
                System.err.println("配置文件 generator.properties 不存在，使用默认配置");
                setDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("加载配置文件失败，使用默认配置：" + e.getMessage());
            setDefaultProperties();
        }
    }

    /**
     * 设置默认配置
     */
    private static void setDefaultProperties() {
        properties.setProperty("database.url", "jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false");
        properties.setProperty("database.username", "root");
        properties.setProperty("database.password", "password");
        properties.setProperty("database.driver", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("database.schema", "your_database");

        properties.setProperty("generator.author", "wen");
        properties.setProperty("generator.basePackage", "com.indiegeeker");
        properties.setProperty("generator.outputDir", "/src/main/java");

        properties.setProperty("package.entity", "pojo.entity");
        properties.setProperty("package.mapper", "mapper");
        properties.setProperty("package.service", "service");
        properties.setProperty("package.serviceImpl", "service.impl");
        properties.setProperty("package.controller", "controller");
        properties.setProperty("package.xml", "mapper.xml");
    }



    /**
     * 获取字段填充策略
     */
    private static List<Column> getTableFills() {
        List<Column> tableFills = new ArrayList<>();

        // 创建时间字段
        String createTimeFields = getProperty("fill.createTimeFields", "");
        if (!createTimeFields.isEmpty()) {
            Arrays.stream(createTimeFields.split(","))
                    .map(String::trim)
                    .filter(field -> !field.isEmpty())
                    .forEach(field -> tableFills.add(new Column(field, FieldFill.INSERT)));
        }

        // 更新时间字段
        String updateTimeFields = getProperty("fill.updateTimeFields", "");
        if (!updateTimeFields.isEmpty()) {
            Arrays.stream(updateTimeFields.split(","))
                    .map(String::trim)
                    .filter(field -> !field.isEmpty())
                    .forEach(field -> tableFills.add(new Column(field, FieldFill.INSERT_UPDATE)));
        }

        return tableFills;
    }

    /**
     * 获取需要包含的表名
     */
    private static List<String> getIncludeTables() {
        List<String> tableNames = new ArrayList<>();
        String tablesStr = getProperty("tables.include", "");
        
        if (!tablesStr.isEmpty()) {
            Arrays.stream(tablesStr.split(","))
                    .map(String::trim)
                    .filter(table -> !table.isEmpty())
                    .forEach(tableNames::add);
        }
        
        return tableNames;
    }

    /**
     * 获取表前缀
     */
    private static List<String> getTablePrefixes() {
        List<String> prefixes = new ArrayList<>();
        String prefixStr = getProperty("tables.prefix", "");
        
        if (!prefixStr.isEmpty()) {
            Arrays.stream(prefixStr.split(","))
                    .map(String::trim)
                    .filter(prefix -> !prefix.isEmpty())
                    .forEach(prefixes::add);
        }
        
        return prefixes;
    }

    /**
     * 获取字符串属性
     */
    private static String getProperty(String key) {
        return properties.getProperty(key, "");
    }

    /**
     * 获取字符串属性（带默认值）
     */
    private static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 获取布尔属性
     */
    private static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
} 