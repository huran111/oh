package com.tykj.carwash.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tykj.core.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @program: tykj-system
 * @description: 预定信息
 * @author: Mr.Zhang
 * @create: 2019-06-20 15:08
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("bas_reserve")
public class Reserve extends BaseEntity {
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	@TableField("create_time")
	private Date createTime;

	/**
	 * 预约时间 格式9:00-10:00
	 */
	@TableField("reserve_time")
	private String reserveTime;

	/**
	 * 店铺id
	 */
	@TableField("store_id")
	private Integer storeId;

	/**
	 * 店铺手机号
	 */
	@TableField("store_phone")
	private String storePhone;

	/**
	 * 用户openId
	 */
	@TableField("open_id")
	private String openId;
	/**
	 * 用户手机号
	 */
	@TableField("user_phone")
	private String userPhone;
	/**
	 * 预约状态0已预约1已取消
	 */
	@TableField("is_reserve")
	private Integer isReserve;
}
