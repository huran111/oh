package com.tykj.config;

import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常
 * @author huran
 */
@Slf4j
@ControllerAdvice
public class GlobalDefultExceptionHandler {

    /**
     *声明要捕获的异常
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResponse<?> defultExcepitonHandler(HttpServletRequest request, Exception e) {
        if (e instanceof BusinessException) {
            log.error(e.getMessage());
            BusinessException businessException = (BusinessException) e;
            return new ApiResponse(businessException.getApiCode(),businessException.getMessage());
        }
        //未知错误
        return ApiResponse.error();
    }
}
