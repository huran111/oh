package com.tykj.wx.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tykj.wx.entity.TmpqrcodeRecord;
import com.tykj.wx.service.ITmpqrcodeRecordService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tykj.core.web.BaseController;
import java.util.List;

/**
* <p>
    *  前端控制器
    * </p>
*
* @author huran
* @since 2019-06-12
*/
@RestController
@RequestMapping("/rest/wx/tmpqrcode-record")
    public class TmpqrcodeRecordController extends BaseController<ITmpqrcodeRecordService,TmpqrcodeRecord> {

}
