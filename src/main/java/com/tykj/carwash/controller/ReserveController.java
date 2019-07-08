package com.tykj.carwash.controller;

import com.alibaba.fastjson.JSONObject;
import com.tykj.aliyun.controller.SendSms;
import com.tykj.carwash.dto.ReserveDTO;
import com.tykj.carwash.entity.Reserve;
import com.tykj.carwash.entity.Shops;
import com.tykj.carwash.service.ReserveService;
import com.tykj.carwash.service.ShopsService;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.utils.AESUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @program: tykj-system
 * @description: 预约
 * @author: Mr.Zhang
 * @create: 2019-06-20 15:56
 **/
@RestController
@RequestMapping("/rest/wx/carwash")
public class ReserveController {
	@Resource
	private ReserveService reserveService;
	@Resource
	private ShopsService shopsService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private SendSms sendSms;

	@PostMapping("/save")
	public ApiResponse save(@RequestBody ReserveDTO reserveDTO) throws Exception {
		Shops shop = shopsService.getById(reserveDTO.getStoreId());
		if (null == shop) {
			return new ApiResponse(ApiCode.OPERATOR_FAIL);
		}
		String sessionKey = stringRedisTemplate.opsForValue().get("sessionKey:" + reserveDTO.getOpenId());
		String info = AESUtils.decrypt(reserveDTO.getEncryptedData(), sessionKey, reserveDTO.getIv(), "UTF-8");
		String userPhone = (String) JSONObject.parseObject(info).get("phoneNumber");
		Reserve reserve = new Reserve();
		reserve.setCreateTime(new Date()).setReserveTime(reserveDTO.getReserveTime()).
				setUserPhone(userPhone).setOpenId(reserveDTO.getOpenId()).setStoreId(shop.getId()).setStorePhone(shop.getPhone());
		reserveService.save(reserve);
		ApiResponse apiResponse = this.sendSms.sendReserveMsg(userPhone, shop.getPhone(), reserveDTO.getReserveTime(), shop.getStoreName());
		if (apiResponse.getStatus() == 200) {
			return new ApiResponse(ApiCode.OPERATOR_SUCCESS);
		}
		return new ApiResponse(ApiCode.OPERATOR_FAIL);

	}

	@GetMapping("/list")
	public ApiResponse list() {
		return null;
	}
}
