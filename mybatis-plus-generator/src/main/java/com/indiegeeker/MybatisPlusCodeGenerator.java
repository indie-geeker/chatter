package com.indiegeeker;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * MyBatis Plus 代码生成器
 * 适用于 MyBatis Plus 3.5.12 版本
 * 
 * 功能特性：
 * - 支持多种数据库（MySQL、PostgreSQL、Oracle、SQLServer等）
 * - 自动生成 Entity、Mapper、Service、Controller
 * - 支持自定义包名和输出路径
 * - 支持字段填充策略
 * - 支持 Lombok 注解
 * - 支持 Swagger 注解
 * 
 * @author wen
 * @date 2025/6/26
 **/
public class MybatisPlusCodeGenerator {

    /**
     * 数据库配置
     */
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "password";
    private static final String DATABASE_DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * 代码生成基础配置
     */
    private static final String AUTHOR = "wen";  // 作者名
    private static final String BASE_PACKAGE = "com.indiegeeker";  // 基础包名
    private static final String OUTPUT_DIR = System.getProperty("user.dir") + "/src/main/java";  // 输出目录

    public static void main(String[] args) {
        // 获取需要生成的表名
        List<String> tableNames = getTableNames();
        
        // 使用 FastAutoGenerator 快速生成代码
        FastAutoGenerator.create(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.outputDir(OUTPUT_DIR)  // 输出目录
                            .author(AUTHOR)  // 作者名
                            .enableSwagger()  // 开启 Swagger 注解
                            .dateType(DateType.TIME_PACK)  // 时间策略
                            .commentDate("yyyy-MM-dd");  // 注释日期格式
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(BASE_PACKAGE)  // 父包名
                            .entity("pojo.entity")  // Entity 包名
                            .mapper("mapper")  // Mapper 包名
                            .service("service")  // Service 包名
                            .serviceImpl("service.impl")  // ServiceImpl 包名
                            .controller("controller");  // Controller 包名
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(tableNames)  // 包含的表名
                            .addTablePrefix("t_", "sys_")  // 过滤表前缀
                            
                            // Entity 策略配置
                            .entityBuilder()
                            .enableLombok()  // 启用 Lombok
                            .enableTableFieldAnnotation()  // 启用字段注解
                            .enableChainModel()  // 启用链式模型
                            .logicDeleteColumnName("deleted")  // 逻辑删除字段
                            .naming(NamingStrategy.underline_to_camel)  // 表名生成策略
                            .columnNaming(NamingStrategy.underline_to_camel)  // 字段生成策略
                            .addTableFills(new Column("create_time", FieldFill.INSERT))  // 创建时间填充
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))  // 更新时间填充
                            .idType(IdType.ASSIGN_ID)  // 主键策略
                            
                            // Mapper 策略配置
                            .mapperBuilder()
                            .enableMapperAnnotation()  // 启用 @Mapper 注解
                            .enableBaseResultMap()  // 启用 BaseResultMap
                            .enableBaseColumnList()  // 启用 BaseColumnList
                            
                            // Service 策略配置
                            .serviceBuilder()
                            
                            // Controller 策略配置
                            .controllerBuilder()
                            .enableRestStyle();  // 启用 REST 风格
                })
                // 模板引擎配置
                .templateEngine(new VelocityTemplateEngine())
                // 执行生成
                .execute();

        System.out.println("代码生成完成！");
    }



    /**
     * 获取需要生成的表名
     * 可以通过控制台输入或者直接在代码中指定
     */
    private static List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        
        // 方式一：控制台输入（推荐用于单次生成）
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入要生成的表名（多个表名用逗号分隔，直接回车生成所有表）：");
        String input = scanner.nextLine().trim();
        
        if (input.isEmpty()) {
            // 生成所有表（不推荐在生产环境使用）
            System.out.println("将生成所有表的代码...");
            // 这里可以通过数据库查询获取所有表名，暂时留空表示生成所有表
        } else {
            // 解析输入的表名
            String[] tables = input.split(",");
            for (String table : tables) {
                tableNames.add(table.trim());
            }
        }
        
        // 方式二：直接在代码中指定（推荐用于批量生成）
        // tableNames.add("user");
        // tableNames.add("role");
        // tableNames.add("permission");
        
        return tableNames;
    }

    /**
     * 针对不同数据库的配置示例
     */
    
    /**
     * 快速生成代码示例 - MySQL 数据库
     */
    public static void generateMySqlExample() {
        FastAutoGenerator.create(
                "jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false",
                "root",
                "password"
        ).globalConfig(builder -> {
            builder.author("wen")
                    .outputDir(System.getProperty("user.dir") + "/src/main/java")
                    .enableSwagger();
        }).packageConfig(builder -> {
            builder.parent("com.indiegeeker")
                    .entity("pojo.entity")
                    .mapper("mapper")
                    .service("service")
                    .serviceImpl("service.impl")
                    .controller("controller");
        }).strategyConfig(builder -> {
            builder.addInclude("user", "role")  // 指定表名
                    .entityBuilder()
                    .enableLombok()
                    .enableChainModel()
                    .enableTableFieldAnnotation()
                    .mapperBuilder()
                    .enableMapperAnnotation()
                    .enableBaseResultMap()
                    .enableBaseColumnList()
                    .controllerBuilder()
                    .enableRestStyle();
        }).execute();
    }
    
    /**
     * 快速生成代码示例 - PostgreSQL 数据库
     */
    public static void generatePostgreSqlExample() {
        FastAutoGenerator.create(
                "jdbc:postgresql://localhost:5432/your_database",
                "postgres",
                "password"
        ).globalConfig(builder -> {
            builder.author("wen")
                    .outputDir(System.getProperty("user.dir") + "/src/main/java");
        }).packageConfig(builder -> {
            builder.parent("com.indiegeeker");
        }).strategyConfig(builder -> {
            builder.addInclude("user", "role")
                    .entityBuilder()
                    .enableLombok();
        }).execute();
    }
}
