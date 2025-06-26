package com.indiegeeker.exceptions;

import com.indiegeeker.base.BaseJSONResult;
import com.indiegeeker.enums.ResponseStatusEnum;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * Desc:
 * Author: wen
 * Date: 2025/6/25
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public BaseJSONResult<Void> handleBaseException(BaseException ex) {
        return BaseJSONResult.error(ex);
    }

    @ExceptionHandler(Exception.class)
    public BaseJSONResult<Void> handleGenericException(Exception ex) {
        return BaseJSONResult.error(ResponseStatusEnum.SYSTEM_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseJSONResult<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String message = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return BaseJSONResult.error(ResponseStatusEnum.FAILED.code, message);
    }
}
