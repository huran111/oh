package com.tykj.wx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.tykj.core.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author huran
 * @since 2019-06-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysThirdReqLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 请求地址
     */
    @TableField("req_url")
    private String reqUrl;

    /**
     * 请求类型
     */
    @TableField("http_method")
    private String httpMethod;

    /**
     * 请求ip地址
     */
    @TableField("req_ip")
    private String reqIp;

    /**
     * 类方法
     */
    @TableField("class_method")
    private String classMethod;

    /**
     * 请求参数
     */
    @TableField("req_args")
    private String reqArgs;

    /**
     * 请求时间
     */
    @TableField("req_time")
    private String reqTime;

    /**
     * 方法内容
     */
    @TableField("ret_args")
    private String retArgs;

    /**
     * 耗时
     */
    @TableField("com_time")
    private String comTime;

    /**
     * 异常信息
     */
    @TableField("err_msg")
    private String errMsg;

    /**
     * 标识
     */
    @TableField("flag")

    private String flag;
    @TableField("msg")

    private String msg;
}