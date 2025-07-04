package com.indiegeeker.core.exceptions;

import com.indiegeeker.core.enums.ResponseStatusEnum;

/**
 * 业务逻辑异常
 */
public class BusinessException extends BaseException {
    
    public BusinessException(ResponseStatusEnum statusEnum) {
        super(statusEnum);
    }
    
    public BusinessException(ResponseStatusEnum statusEnum, String message) {
        super(statusEnum, message);
    }
    
    public BusinessException(String message) {
        super(ResponseStatusEnum.BUSINESS_RULE_VIOLATION, message);
    }
} 