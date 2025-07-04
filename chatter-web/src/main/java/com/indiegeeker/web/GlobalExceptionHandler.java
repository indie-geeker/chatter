package com.indiegeeker.web;

import com.indiegeeker.core.base.BaseJSONResult;
import com.indiegeeker.core.enums.ResponseStatusEnum;
import com.indiegeeker.core.exceptions.BaseException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
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


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseJSONResult<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = ex.getMessage();
        return BaseJSONResult.error(ResponseStatusEnum.FAILED.code, message);
    }
}
