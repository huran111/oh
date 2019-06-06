package com.tykj.wx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private String reqUrl;

    /**
     * 请求类型
     */
    private String httpMethod;

    /**
     * 请求ip地址
     */
    private String reqIp;

    /**
     * 类方法
     */
    private String classMethod;

    /**
     * 请求参数
     */
    private String reqArgs;

    /**
     * 请求时间
     */
    private String reqTime;

    /**
     * 方法内容
     */
    private String retArgs;

    /**
     * 耗时
     */
    private String comTime;

    /**
     * 异常信息
     */
    private String errMsg;

    /**
     * 标识
     */
    private String flag;


}