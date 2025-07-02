package com.indiegeeker.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.indiegeeker.enums.ResponseStatusEnum;
import com.indiegeeker.exceptions.BaseException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一API响应结果
 * Author: wen
 * Date: 2025/6/24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一API响应结果")
public class BaseJSONResult<T> implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "响应状态码", example = "200", required = true)
    private Integer code;
    @Schema(description = "响应消息", example = "操作成功", required = true)
    private String msg;
    @Schema(description = "响应数据")
    private T data;
    private LocalDateTime timestamp;
    
    // 带时间戳的构造器
    public BaseJSONResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }
    
    public BaseJSONResult(ResponseStatusEnum statusEnum) {
        this.code = statusEnum.getCode();
        this.msg = statusEnum.getMessage();
        this.timestamp = LocalDateTime.now();
    }

    public BaseJSONResult(ResponseStatusEnum statusEnum, T data) {
        this.code = statusEnum.getCode();
        this.msg = statusEnum.getMessage();
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public BaseJSONResult(ResponseStatusEnum statusEnum, String msg) {
        this.code = statusEnum.getCode();
        this.msg = msg;
        this.timestamp = LocalDateTime.now();
    }

    // === 静态工厂方法 ===
    
    // 成功响应
    public static <T> BaseJSONResult<T> ok() {
        return new BaseJSONResult<>(ResponseStatusEnum.SUCCESS);
    }
    
    public static <T> BaseJSONResult<T> ok(T data) {
        return new BaseJSONResult<>(ResponseStatusEnum.SUCCESS, data);
    }
    
    public static <T> BaseJSONResult<T> ok(String msg, T data) {
        return new BaseJSONResult<>(ResponseStatusEnum.SUCCESS.getCode(), msg, data);
    }
    
    // 失败响应
    public static <T> BaseJSONResult<T> error() {
        return new BaseJSONResult<>(ResponseStatusEnum.FAILED);
    }
    
    public static <T> BaseJSONResult<T> error(String msg) {
        return new BaseJSONResult<>(ResponseStatusEnum.FAILED, msg);
    }
    
    public static <T> BaseJSONResult<T> error(ResponseStatusEnum statusEnum) {
        return new BaseJSONResult<>(statusEnum);
    }
    
    public static <T> BaseJSONResult<T> error(Integer code, String msg) {
        return new BaseJSONResult<>(code, msg, null);
    }
    
    // 根据异常创建响应
    public static <T> BaseJSONResult<T> error(BaseException ex) {
        return new BaseJSONResult<>(ex.getCode(), ex.getMessage(), null);
    }
    
    // 判断是否成功
    @JsonIgnore
    public boolean isSuccess() {
        return ResponseStatusEnum.SUCCESS.getCode().equals(this.code);
    }
}
