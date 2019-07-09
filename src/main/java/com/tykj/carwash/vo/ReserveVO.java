package com.tykj.carwash.vo;

import com.tykj.core.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @program: tykj-system
 * @description: 预约记录
 * @author: Mr.Zhang
 * @create: 2019-07-08 18:05
 **/
@Data
@Accessors(chain = true)
public class ReserveVO extends BaseEntity {

	private Date createTime;

	/**
	 * 预约时间 格式9:00-10:00
	 */
	private String reserveTime;

	/**
	 * 店铺id
	 */
	private Integer storeId;

	/**
	 * 店铺手机号
	 */
	private String storePhone;

	/**
	 * 经度
	 */
	private Double longitude;
	/**
	 * 纬度
	 */
	private Double latitude;
	/**
	 * 位置
	 */
	private String address;

	private String phone;
	/**
	 * 店铺等级
	 */
	private Integer grade;
	private Integer flag;
	/**
	 * 店名
	 */
	private String storeName;

	private String image;

	/**
	 * 距离(米)前端展示
	 */
	private Double distance;
	/**
	 * 销量
	 */
	private Integer sales;

	/**
	 * 参考价格
	 */
	private String refPrice;

}
