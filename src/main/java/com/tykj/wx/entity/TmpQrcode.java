package com.tykj.wx.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.tykj.core.BaseEntity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author huran
 * @since 2019-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("wx_tmp_qrcode")
public class TmpQrcode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 绑定小程序的id 有效期
     */
    @TableField("openId")
    private String openId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    /**
     * 二维码地址
     */
    @TableField("img_url")
    private String imgUrl;
    @TableField("is_switch")
    private String isSwitch;
    @TableField("qr_param")
    private String qrParam;
    @TableField("phone_num")

    private String phoneNum;
    @TableField("plate_num")

    private String plateNum;

}