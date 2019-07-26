package com.tykj.wx.controller;


import com.tykj.wx.entity.JobParamRecord;
import com.tykj.wx.service.IJobParamRecordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tykj.core.web.BaseController;

/**
* <p>
    * job生成的线下二维码记录 前端控制器
    * </p>
*
* @author huran
* @since 2019-07-26
*/
@RestController
@RequestMapping("rest/wx/job-param-record")
    public class JobParamRecordController extends BaseController<IJobParamRecordService,JobParamRecord> {

}
