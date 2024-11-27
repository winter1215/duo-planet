package com.winter.duo.config.exception;

import cn.hutool.core.exceptions.ValidateException;
import com.winter.duo.common.BaseResponse;
import com.winter.duo.common.ErrorCode;
import com.winter.duo.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidateException.class)
    public BaseResponse<?> validateExceptionHandler(ValidateException e) {
        log.error("ValidateException", e);
        return R.error(ErrorCode.NOT_LOGIN_ERROR, "未登录");
    }
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return R.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return R.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
