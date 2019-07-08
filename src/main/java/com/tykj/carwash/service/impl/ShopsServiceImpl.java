package com.tykj.carwash.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tykj.carwash.entity.Shops;
import com.tykj.carwash.mapper.ShopsMapper;
import com.tykj.carwash.service.ShopsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ShopsServiceImpl extends ServiceImpl<ShopsMapper, Shops> implements ShopsService {
	@Resource
	private ShopsMapper shopsMapper;

}
