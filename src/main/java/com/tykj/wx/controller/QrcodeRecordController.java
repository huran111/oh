package com.tykj.wx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.common.SysConstant;
import com.tykj.wx.entity.QrcodeRecord;
import com.tykj.wx.entity.TmpqrcodeRecord;
import com.tykj.wx.service.IQrcodeRecordService;

import com.tykj.wx.service.ITmpqrcodeRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tykj.core.web.BaseController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huran
 * @since 2019-06-12
 */
@RestController
@RequestMapping("rest/wx/qrcoderecord")
public class QrcodeRecordController extends BaseController<IQrcodeRecordService, QrcodeRecord> {
    @Autowired
    ITmpqrcodeRecordService tmpqrcodeRecordService;
    @Autowired
    IQrcodeRecordService qrcodeRecordService;

    @GetMapping(value = "viewRecord")
    public ApiResponse viewRecord(@RequestParam(value = "qrParam") String qrParam) {
        if (qrParam.contains(SysConstant.TMP_QRPARAM)) {
            List<TmpqrcodeRecord> qrcodeRecords = tmpqrcodeRecordService.list(new QueryWrapper<TmpqrcodeRecord>()
                    .lambda().eq(TmpqrcodeRecord::getQrParam, qrParam));
            if (CollectionUtils.isNotEmpty(qrcodeRecords)) {
              return   new ApiResponse<>(ApiCode.REQUEST_SUCCESS, qrcodeRecords);
            }else {
                return   new ApiResponse<>(ApiCode.REQUEST_SUCCESS, new ArrayList<>());
            }
        } else {
            List<QrcodeRecord> qrcodeRecords = qrcodeRecordService.list(new QueryWrapper<QrcodeRecord>()
                    .lambda().eq(QrcodeRecord::getQrParam, qrParam));
            if (CollectionUtils.isNotEmpty(qrcodeRecords)) {
                return   new ApiResponse<>(ApiCode.REQUEST_SUCCESS, qrcodeRecords);
            }else {
                return   new ApiResponse<>(ApiCode.REQUEST_SUCCESS, new ArrayList<>());
            }
        }
    }
}
