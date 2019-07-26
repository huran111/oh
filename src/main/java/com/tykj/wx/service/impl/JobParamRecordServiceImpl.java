package com.tykj.wx.service.impl;

import com.tykj.wx.entity.JobParamRecord;
import com.tykj.wx.mapper.JobParamRecordMapper;
import com.tykj.wx.service.IJobParamRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* <p>
    * job生成的线下二维码记录 服务实现类
    * </p>
*
* @author huran
* @since 2019-07-26
*/
@Service
@Transactional
public class JobParamRecordServiceImpl extends ServiceImpl<JobParamRecordMapper, JobParamRecord> implements IJobParamRecordService {

}
