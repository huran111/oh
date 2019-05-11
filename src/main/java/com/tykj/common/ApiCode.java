package com.tykj.common;


public enum ApiCode {
    SUCCESS(10,"操作成功"),
    ERROR(500,"操作失败"),
    LONG_SUCCESS(200,"登陆成功"),
    NOT_ROLE(13,"无角色"),

    NOT_LOGIN(12,"未登录"),
    EMPTY_PARAM(0,"空数据"),
    SESSION_KEY_FAIL(1,"获取sessionKey失败"),
    CHECK_FAIL(2,"校验失败");
    private int code;
    private String desc;

    ApiCode(int code, String desc){
        this.code = code;
        this.desc = desc;
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
}
