package com.tykj.exception;

import com.tykj.common.ApiCode;

/**
 * 业务类异常
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;
    private ApiCode apiCode;
    public BusinessException() {
    }

    public BusinessException(ApiCode resultEnum,String message) {
        super(resultEnum.getDesc());
        this.apiCode = resultEnum;
        this.message=message;
    }

    public ApiCode getApiCode() {
        return apiCode;
    }

    public void setApiCode(ApiCode apiCode) {
        this.apiCode = apiCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
