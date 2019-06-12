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
    * 
    * </p>
*
* @author huran
* @since 2019-06-12
*/
@Data
    @EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("wx_qrcode_record")
public class QrcodeRecord extends BaseEntity {

private static final long serialVersionUID = 1L;

        @TableId(value = "id", type = IdType.UUID)
private String id;

    @TableField("qrParam")
private String qrParam;

private String phone;

private Date createTime;
    /**
     *  1短信 2电话
     */
    private String flag;
    /**
     * 1 成功 0失败
     */
    private String status;

}