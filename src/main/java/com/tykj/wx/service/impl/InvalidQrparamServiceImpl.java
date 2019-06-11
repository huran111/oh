package com.tykj.wx.service.impl;

import com.tykj.wx.entity.InvalidQrparam;
import com.tykj.wx.mapper.InvalidQrparamMapper;
import com.tykj.wx.service.IInvalidQrparamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author huran
* @since 2019-06-11
*/
@Service
@Transactional
public class InvalidQrparamServiceImpl extends ServiceImpl<InvalidQrparamMapper, InvalidQrparam> implements IInvalidQrparamService {

}
