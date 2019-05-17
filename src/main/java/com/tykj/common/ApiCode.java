package com.tykj.common;

/**
 * @author  huran
 */
public enum ApiCode {
    OPERATOR_SUCCESS(200,"操作成功"),
    ERROR(500,"系统异常"),
    LONG_SUCCESS(200,"成功"),
    DELETE_SUCCESS(200,"删除成功"),
    LOGOUT_SUCCESS(200,"退出成功"),
    OPERATOR_FAIL(500,"操作失败"),
    NOT_ROLE(13,"无角色"),
    REQUEST_SUCCESS(200,"请求成功"),
    NOT_LOGIN(12,"未登录"),
    EMPTY_PARAM(200,0,"空数据"),
    SESSION_KEY_FAIL(200,2,"获取sessionKey失败"),
    CHECK_FAIL(200,1,"校验失败"),
    BINDING(200,1,"已绑定"),
    NOT_BINDING(200,0,"未绑定"),
    IS_ONESELF(200,1000,"自己扫自己"),
    BINDING_SUCCESS(200,"绑定成功");

    private int code;
    private String desc;
    private int status;

    ApiCode(int status,int code, String desc){
        this.code = code;
        this.desc = desc;
        this.status=status;
    }
    ApiCode(int status , String desc){
        this.code = code;
        this.desc = desc;
        this.status=status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
