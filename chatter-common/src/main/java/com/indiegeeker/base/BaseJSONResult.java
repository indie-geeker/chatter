package com.indiegeeker.base;

import com.indiegeeker.enums.ResponseStatusEnum;

import java.io.Serializable;

/**
 * Desc: 自定义响应数据
 * Author: wen
 * Date: 2025/6/24
 **/
public class BaseJSONResult<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    // 无参构造器
    public BaseJSONResult() {}

    // 带参构造器
    public BaseJSONResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public BaseJSONResult(ResponseStatusEnum responseStatusEnum) {
      this.code = responseStatusEnum.code;
      this.msg = responseStatusEnum.message;
    }

    public BaseJSONResult(ResponseStatusEnum responseStatusEnum, T data) {
        this.code = responseStatusEnum.code;
        this.msg = responseStatusEnum.message;
        this.data = data;
    }

    public BaseJSONResult(ResponseStatusEnum responseStatusEnum,String msg) {
        this.code = responseStatusEnum.code;
        this.msg = msg;
    }

    // 成功，无数据
    public static <T> BaseJSONResult<T> success() {
        return new BaseJSONResult<T>(ResponseStatusEnum.SUCCESS);
    }
    // 成功，返回数据
    public static <T> BaseJSONResult<T> success(T data) {
        return new BaseJSONResult<T>(ResponseStatusEnum.SUCCESS, data);
    }
    // 失败，无信息
    public static <T> BaseJSONResult<T> error() {
        return new BaseJSONResult<T>(ResponseStatusEnum.FAILED);
    }
    // 失败，返回信息
    public static <T> BaseJSONResult<T> error(String msg) {
        return new BaseJSONResult<T>(ResponseStatusEnum.FAILED,msg);
    }

    // 失败，返回错误枚举类
    public static <T> BaseJSONResult<T> error(ResponseStatusEnum responseStatusEnum) {
        return new BaseJSONResult<T>(responseStatusEnum);
    }

    // 失败，自定义错误信息
    public static <T> BaseJSONResult<T> error(int code, String msg) {
        return new BaseJSONResult<T>(code,msg,null);
    }




}
