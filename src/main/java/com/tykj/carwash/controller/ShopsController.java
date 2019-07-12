package com.tykj.carwash.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.tykj.carwash.entity.Reserve;
import com.tykj.carwash.entity.Shops;
import com.tykj.carwash.service.ReserveService;
import com.tykj.carwash.service.ShopsService;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.core.web.BaseController;
import com.tykj.utils.LngLatUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.tykj.common.InitShopsProcessor.SHOPS_LIST;


/**
 * @author Mr.zhang
 * @Description 洗车商家
 * @date
 **/
@RestController
@RequestMapping("/rest/wx/shops")
public class ShopsController extends BaseController<ShopsService, Shops> {
	@Resource
	private ShopsService shopsService;
	@Resource
	private ReserveService reserveService;

	/**
	 * 车行信息
	 *
	 * @param lng       经度
	 * @param lat       纬度
	 * @param storeName 店铺名称(搜索使用)
	 * @return
	 */
	@GetMapping("/list")
	public ApiResponse findAll(@RequestParam(value = "longitude") String lng,
							   @RequestParam(value = "latitude") String lat,
							   @RequestParam(value = "storeName", required = false) String storeName) {
		List<Shops> shopsList = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(SHOPS_LIST)) {
			shopsList.addAll(SHOPS_LIST);
			if (StringUtils.isNotBlank(storeName)) {
				shopsList = shopsList.stream().filter(shops -> shops.getStoreName().contains(storeName)).collect(Collectors.toList());
			}
			shopsList.forEach(shops -> {
				double distance = LngLatUtils.getDistance(Double.parseDouble(lng), Double.parseDouble(lat), shops.getLongitude(), shops.getLatitude());
				shops.setDistance(distance);
			});
		}
		Collections.sort(shopsList);
		return new ApiResponse(ApiCode.OPERATOR_SUCCESS, shopsList);
	}

}
