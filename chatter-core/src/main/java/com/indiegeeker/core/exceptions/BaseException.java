package com.indiegeeker.core.exceptions;

import com.indiegeeker.core.enums.ResponseStatusEnum;
import lombok.Getter;

/**
 * 自定义业务异常基类
 * Author: wen
 * Date: 2025/6/24
 **/
@Getter
public class BaseException extends RuntimeException {
    
    private final Integer code;
    private final String message;
    private final ResponseStatusEnum statusEnum;
    
    // 使用枚举构造异常
    public BaseException(ResponseStatusEnum statusEnum) {
        super(statusEnum.getMessage());
        this.code = statusEnum.getCode();
        this.message = statusEnum.getMessage();
        this.statusEnum = statusEnum;
    }
    
    // 使用枚举 + 自定义消息
    public BaseException(ResponseStatusEnum statusEnum, String customMessage) {
        super(customMessage);
        this.code = statusEnum.getCode();
        this.message = customMessage;
        this.statusEnum = statusEnum;
    }
    
    // 使用枚举 + 原因异常
    public BaseException(ResponseStatusEnum statusEnum, Throwable cause) {
        super(statusEnum.getMessage(), cause);
        this.code = statusEnum.getCode();
        this.message = statusEnum.getMessage();
        this.statusEnum = statusEnum;
    }
    
    // 使用枚举 + 自定义消息 + 原因异常
    public BaseException(ResponseStatusEnum statusEnum, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.code = statusEnum.getCode();
        this.message = customMessage;
        this.statusEnum = statusEnum;
    }
}
