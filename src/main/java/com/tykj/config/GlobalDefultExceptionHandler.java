package com.tykj.config;

import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
            e.printStackTrace();
            BusinessException businessException = (BusinessException) e;
            return new ApiResponse(ApiCode.EMPTY_PARAM,businessException.getMessage());
        }
        if(e instanceof MethodArgumentNotValidException){
            e.printStackTrace();
            log.info(e.getMessage());
            e.printStackTrace();
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;
            return new ApiResponse(ApiCode.EMPTY_PARAM,validException.getMessage());
        }else {
            e.printStackTrace();
        }
        //未知错误
        return ApiResponse.error();
    }
}
