package com.tykj.common;

/**
 * @author  huran
 * @param <T>
 */
public class ApiResponse<T> {
    public int code;

    public String msg;

    public T data;

    public long totalTime;

    public ApiResponse(ApiCode ApiCode) {
        this.code = ApiCode.getCode();
        this.msg = ApiCode.getDesc();
    }

    public ApiResponse(ApiCode ApiCode, T data) {
        this.code = ApiCode.getCode();
        this.msg = ApiCode.getDesc();
        this.data = data;
    }

    public ApiResponse(ApiCode ApiCode, String msg) {
        this.code = ApiCode.getCode();
        this.msg = msg;
    }

    public ApiResponse(ApiCode ApiCode, String msg, T data) {
        this.code = ApiCode.getCode();
        this.msg = msg;
        this.data = data;
    }

    public static ApiResponse success() {
        return new ApiResponse(ApiCode.SUCCESS);
    }
    public static ApiResponse error() {
        return new ApiResponse(ApiCode.ERROR);
    }
    public static ApiResponse build(ApiCode ApiCode) {
        return new ApiResponse(ApiCode);
    }
    public static<T> ApiResponse<T> build(ApiCode ApiCode, T data) {
        return new ApiResponse(ApiCode,data);
    }
    public static ApiResponse build(ApiCode ApiCode, String msg) {
        return new ApiResponse(ApiCode,msg);
    }
    public static<T> ApiResponse<T> build(ApiCode ApiCode, String msg, T data) {
        return new ApiResponse(ApiCode,msg,data);
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

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

}
