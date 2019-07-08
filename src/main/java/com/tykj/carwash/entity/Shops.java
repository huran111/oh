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
 * @program: 车行信息
 * @description:
 * @author: Mr.Zhang
 * @create: 2019-06-18 14:32
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("bas_shops")
public class Shops extends BaseEntity implements Comparable<Shops> {
	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;
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
	private Integer grade;
	private Integer flag;
	/**
	 * 店名
	 */
	@TableField("store_name")
	private String storeName;
	private String image;
	@TableField("create_time")
	private Date createTime;
	/**
	 * 距离(米)前端展示
	 */
	@TableField(exist = false)
	private Double distance;
	/**
	 * 销量
	 */
	@TableField(exist = false)
	private Integer sales;

	@Override
	public int compareTo(Shops o) {
		if (this.distance > o.distance) {
			return 1;
		} else if (this.distance < o.distance) {
			return -1;
		} else {
			if (this.grade > o.grade) {
				return -1;
			} else if (this.grade < o.grade) {
				return 1;
			} else {
				if (this.sales > o.sales) {
					return -1;
				} else if (this.sales < o.sales) {
					return 1;
				}
			}
		}
		return 0;
	}
}
