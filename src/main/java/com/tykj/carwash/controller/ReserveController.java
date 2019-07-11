package com.tykj.carwash.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.tykj.aliyun.controller.SendSms;
import com.tykj.carwash.dto.ParameterDTO;
import com.tykj.carwash.entity.Reserve;
import com.tykj.carwash.entity.Shops;
import com.tykj.carwash.service.ReserveService;
import com.tykj.carwash.service.ShopsService;
import com.tykj.carwash.vo.ReserveVO;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.utils.AESUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
	@Resource
	private SendSms sendSms;

	/**
	 * 保存预约洗车记录
	 *
	 * @param parameterDTO 参数
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/save")
	public ApiResponse save(@RequestBody ParameterDTO parameterDTO) throws Exception {

		QueryWrapper<Reserve> condition = new QueryWrapper<>();
		condition.lambda().eq(Reserve::getOpenId, parameterDTO.getOpenId());
		condition.lambda().apply("date(create_time) = curdate()");
		int count = reserveService.count(condition);
		if (count > 3) {
			return new ApiResponse(ApiCode.RECEVRER_FAIL);
		}
		Shops shop = shopsService.getById(parameterDTO.getStoreId());
		if (null == shop) {
			return new ApiResponse(ApiCode.OPERATOR_FAIL);
		}
		String sessionKey = stringRedisTemplate.opsForValue().get("sessionKey:" + parameterDTO.getOpenId());
		String info = AESUtils.decrypt(parameterDTO.getEncryptedData(), sessionKey, parameterDTO.getIv(), "UTF-8");
		String userPhone = (String) JSONObject.parseObject(info).get("phoneNumber");
		Reserve reserve = new Reserve();
		reserve.setCreateTime(new Date()).setReserveTime(parameterDTO.getReserveTime()).
				setUserPhone(userPhone).setOpenId(parameterDTO.getOpenId()).setStoreId(shop.getId()).setStorePhone(shop.getPhone());
		reserveService.save(reserve);
		ApiResponse apiResponse = this.sendSms.sendReserveMsg(userPhone, shop.getPhone(), parameterDTO.getReserveTime(), shop.getStoreName());
		if (apiResponse.getStatus() == 200) {
			return new ApiResponse(ApiCode.OPERATOR_SUCCESS);
		}
		return new ApiResponse(ApiCode.OPERATOR_FAIL);

	}

	/**
	 * 获取用户续约记录
	 *
	 * @param openId 用户openid
	 * @param lng    经度
	 * @param lat    纬度
	 * @return
	 */
	@GetMapping("/list")
	public ApiResponse list(
			@RequestParam("openId") String openId, @RequestParam(value = "longitude", required = false) String lng,
			@RequestParam(value = "latitude", required = false) String lat) {
		QueryWrapper<Reserve> condition = new QueryWrapper<>();
		condition.lambda().eq(Reserve::getOpenId, openId);
		condition.lambda().eq(Reserve::getIsReserve, 0);
		condition.lambda().apply("create_time >= now()-interval 3 day");
		condition.lambda().orderByDesc(true, Reserve::getCreateTime);
		List<Reserve> reserves = reserveService.list(condition);
		if (CollectionUtils.isEmpty(reserves)) {
			return new ApiResponse(ApiCode.OPERATOR_SUCCESS, reserves);
		}
		Shops shop = shopsService.getById(reserves.get(0).getStoreId());
		List<ReserveVO> reserveArrayList = Lists.newArrayList();
		for (Reserve reserve : reserves) {
			ReserveVO reserveVO = new ReserveVO();
			//double distance = LngLatUtils.getDistance(Double.parseDouble(lng), Double.parseDouble(lat), shop.getLongitude(), shop.getLatitude());
			reserveVO.setAddress(shop.getAddress()).setGrade(shop.getGrade()).setLatitude(shop.getLatitude()).
					setLongitude(shop.getLongitude()).setImage(shop.getImage()).setPhone(shop.getPhone()).setRefPrice(shop.getRefPrice()).setSales(shop.getSales()).
					setStoreId(shop.getId()).setReserveTime(reserve.getReserveTime()).setStoreName(shop.getStoreName()).setId(reserve.getId());
			reserveArrayList.add(reserveVO);
		}
		return new ApiResponse(ApiCode.OPERATOR_SUCCESS, reserveArrayList);
	}

	/**
	 * 取消预约
	 *
	 * @param id 预约记录的id
	 * @return
	 */
	@GetMapping("/cancelReserve")
	public ApiResponse cancelReserve(@RequestParam("id") String id) {
		Reserve reserve = reserveService.getById(id);
		if (null == reserve) {
			return new ApiResponse(ApiCode.RECEVEER_RECORD);
		}
		reserve.setIsReserve(1);
		reserveService.updateById(reserve);
		try {
			Shops shop = shopsService.getById(reserve.getStoreId());
			this.sendSms.sendReserveMsg(reserve.getUserPhone(), shop.getPhone(), reserve.getReserveTime(), shop.getStoreName(), "cancel");
		} catch (Exception e) {
			return new ApiResponse(ApiCode.RECEVRER_SUCCESS);
		}
		return new ApiResponse(ApiCode.OPERATOR_SUCCESS);
	}
}
