package com.tykj.wx.service.impl;

import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.mapper.QrcodeMapper;
import com.tykj.wx.service.IQrcodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author huran
* @since 2019-05-11
*/
@Service
@Transactional
public class QrcodeServiceImpl extends ServiceImpl<QrcodeMapper, Qrcode> implements IQrcodeService {

}
