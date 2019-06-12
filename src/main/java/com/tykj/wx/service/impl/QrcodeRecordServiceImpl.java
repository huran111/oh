package com.tykj.wx.service.impl;

import com.tykj.wx.entity.QrcodeRecord;
import com.tykj.wx.mapper.QrcodeRecordMapper;
import com.tykj.wx.service.IQrcodeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author huran
* @since 2019-06-12
*/
@Service
@Transactional
public class QrcodeRecordServiceImpl extends ServiceImpl<QrcodeRecordMapper, QrcodeRecord> implements IQrcodeRecordService {

}
