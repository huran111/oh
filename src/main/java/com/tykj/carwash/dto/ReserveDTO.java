package com.tykj.carwash.dto;

import lombok.Data;

/**
 * @program: tykj-system
 * @description:
 * @author: Mr.Zhang
 * @create: 2019-07-03 20:04
 **/
@Data
public class ReserveDTO {
	private String encryptedData;
	private String reserveTime;
	private String storeId;
	private String iv;
	private String openId;
}
