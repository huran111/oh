package com.tykj.carwash.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tykj.carwash.entity.Reserve;
import com.tykj.carwash.mapper.ReserveMapper;
import com.tykj.carwash.service.ReserveService;
import org.springframework.stereotype.Service;


@Service
public class ReserveServiceImpl extends ServiceImpl<ReserveMapper, Reserve> implements ReserveService {

}
