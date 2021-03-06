package com.tykj.wx.service.impl;

import com.tykj.wx.entity.SysThirdReqLog;
import com.tykj.wx.mapper.SysThirdReqLogMapper;
import com.tykj.wx.service.ISysThirdReqLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author huran
* @since 2019-06-06
*/
@Service
@Transactional
public class SysThirdReqLogServiceImpl extends ServiceImpl<SysThirdReqLogMapper, SysThirdReqLog> implements ISysThirdReqLogService {

}
