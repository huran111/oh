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
@TableName("wx_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @TableField("openId")
    private String openId;
    @TableField("create_time")
    private Date createTime;
    @TableField("nick_name")
    private String nickName;
    @TableField("gender")
    private Integer gender;
    @TableField("city")
    private String city;
    @TableField("province")
    private String province;
    @TableField("country")
    private String country;
    @TableField("avatar_url")
    private String avatarUrl;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", openId='" + openId + '\'' +
                ", createTime=" + createTime +
                ", nickName='" + nickName + '\'' +
                ", gender='" + gender + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", country='" + country + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}