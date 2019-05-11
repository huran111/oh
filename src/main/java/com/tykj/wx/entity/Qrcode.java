package com.tykj.wx.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.tykj.core.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author huran
 * @since 2019-05-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("wx_qrcode")
public class Qrcode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 二维码参数 唯一的
     */
    @TableField("qr_param")
    private String qrParam;


    /**
     * 是否绑定 0未绑定1 绑定
     */
    @TableField("is_binding")
    private String isBinding;

    /**
     * 手机号
     */
    @TableField("phone_num")
    private String phoneNum;

    /**
     * 车牌号
     */
    @TableField("plate_num")
    private String plateNum;

    @TableField("create_time")
    private Date createTime;

}