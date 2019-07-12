package com.tykj.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.tykj.carwash.entity.Reserve;
import com.tykj.carwash.entity.Shops;
import com.tykj.carwash.service.ReserveService;
import com.tykj.carwash.service.ShopsService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: tykj-system
 * @description: 初始化车行信息
 * @author: Mr.Zhang
 * @create: 2019-07-12 16:18
 **/
@Component
public class InitShopsProcessor implements ApplicationListener<ContextRefreshedEvent> {
	public static final List<Shops> SHOPS_LIST = Lists.newArrayList();
	@Resource
	private ShopsService shopsService;
	@Resource
	private ReserveService reserveService;
	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		QueryWrapper<Shops> condition = new QueryWrapper<>();
		condition.lambda().eq(Shops::getFlag, 0);
		List<Shops> shopsList = shopsService.list(condition);
		shopsList.forEach(shop -> {
			int count = reserveService.count(new QueryWrapper<Reserve>().lambda().eq(Reserve::getStoreId, shop.getId()));
			shop.setSales(count);
		});

	}
}
