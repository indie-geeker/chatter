package com.indiegeeker.doc.utils;

import com.indiegeeker.base.BaseJSONResult;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.*;

/**
 * API 响应包装器工具类
 * <p>
 * 提供通用的响应注解，用于统一文档格式
 *
 * @author indiegeeker
 */
public class ApiResponseWrapper {

    /**
     * 成功响应注解
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "操作成功",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            )
    })
    public @interface Success {
        /**
         * 响应描述
         */
        String value() default "操作成功";
    }

    /**
     * 通用响应注解（包含成功和失败）
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "操作成功",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "权限不足",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "服务器内部错误",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            )
    })
    public @interface Common {
    }

    /**
     * 分页响应注解
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "查询成功",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            )
    })
    public @interface Page {
        /**
         * 响应描述
         */
        String value() default "查询成功";
    }

    /**
     * 创建响应注解
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "创建成功",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "资源冲突",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            )
    })
    public @interface Create {
        /**
         * 响应描述
         */
        String value() default "创建成功";
    }

    /**
     * 更新响应注解
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "资源不存在",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            )
    })
    public @interface Update {
        /**
         * 响应描述
         */
        String value() default "更新成功";
    }

    /**
     * 删除响应注解
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "删除成功",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "资源不存在",
                    content = @Content(schema = @Schema(implementation = BaseJSONResult.class))
            )
    })
    public @interface Delete {
        /**
         * 响应描述
         */
        String value() default "删除成功";
    }
} 