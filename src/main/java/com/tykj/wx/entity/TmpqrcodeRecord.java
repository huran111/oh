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
 * @since 2019-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("wx_tmpqrcode_record")
public class TmpqrcodeRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String id;

    @TableField("qrParam")
    private String qrParam;
    private String phone;
    @TableField("create_time")
    private Date createTime;
    private String flag;


}