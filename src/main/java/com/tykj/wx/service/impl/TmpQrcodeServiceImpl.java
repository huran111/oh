package com.tykj.wx.service.impl;

import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.mapper.TmpQrcodeMapper;
import com.tykj.wx.service.ITmpQrcodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author huran
* @since 2019-05-12
*/
@Service
@Transactional
public class TmpQrcodeServiceImpl extends ServiceImpl<TmpQrcodeMapper, TmpQrcode> implements ITmpQrcodeService {

}
