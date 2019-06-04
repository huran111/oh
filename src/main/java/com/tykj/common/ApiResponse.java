package com.tykj.common;

/**
 * @param <T>
 * @author huran
 */
public class ApiResponse<T> {
    public int code;

    public String msg;
    public T data;
    public int status;

    public ApiResponse(ApiCode ApiCode) {
        this.code = ApiCode.getCode();
        this.msg = ApiCode.getDesc();
        this.status = ApiCode.getStatus();
    }

    public ApiResponse(ApiCode ApiCode, T data) {
        this.code = ApiCode.getCode();
        this.msg = ApiCode.getDesc();
        this.data = data;
        this.status = ApiCode.getStatus();

    }

    public ApiResponse(ApiCode ApiCode, String msg, T data) {
        this.code = ApiCode.getCode();
        this.msg = msg;
        this.data = data;
        this.status = ApiCode.getStatus();

    }

    public static ApiResponse success() {
        return new ApiResponse(ApiCode.OPERATOR_SUCCESS);
    }

    public static ApiResponse error() {
        return new ApiResponse(ApiCode.ERROR);
    }

    public static ApiResponse build(ApiCode ApiCode) {
        return new ApiResponse(ApiCode);
    }

    public static <T> ApiResponse<T> build(ApiCode ApiCode, T data) {
        return new ApiResponse(ApiCode, data);
    }

    public static ApiResponse build(ApiCode ApiCode, String msg) {
        return new ApiResponse(ApiCode, msg);
    }

    public static <T> ApiResponse<T> build(ApiCode ApiCode, String msg, T data) {
        return new ApiResponse(ApiCode, msg, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
