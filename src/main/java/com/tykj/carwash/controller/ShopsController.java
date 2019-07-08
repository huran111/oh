package com.tykj.carwash.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tykj.carwash.entity.Reserve;
import com.tykj.carwash.entity.Shops;
import com.tykj.carwash.service.ReserveService;
import com.tykj.carwash.service.ShopsService;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.core.web.BaseController;
import com.tykj.utils.LngLatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;


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
	@Autowired
	private RedisTemplate redisTemplate;

	@GetMapping("/list")
	public ApiResponse findAll(@RequestParam(value = "longitude") String lng,
							   @RequestParam(value = "latitude") String lat,
							   @RequestParam(value = "storeName", required = false) String storeName) {
		QueryWrapper<Shops> condition = new QueryWrapper<>();
		if (StringUtils.isNotBlank(storeName)) {
			condition.lambda().like(Shops::getStoreName, storeName);
		}
		Page<Shops> pageCondition = new Page<>(1, 6);
		IPage<Shops> page = shopsService.page(pageCondition, condition);
		List<Shops> shopsList = page.getRecords();
		shopsList.forEach(data -> {
			double distance = LngLatUtils.getDistance(Double.parseDouble(lng), Double.parseDouble(lat), data.getLongitude(), data.getLatitude());
			data.setDistance(distance);
			int count = reserveService.count(new QueryWrapper<Reserve>().lambda().eq(Reserve::getStoreId, data.getId()));
			data.setSales(count);
		});
		Collections.sort(shopsList);
		redisTemplate.opsForList().leftPush("shopsList", shopsList);

		return new ApiResponse(ApiCode.OPERATOR_SUCCESS, shopsList);

	}

}
