package com.tykj.wx.controller;


import com.jfinal.core.Controller;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import com.tykj.wx.dto.UserInfoDTO;
import com.tykj.wx.service.IQrcodeService;

import com.tykj.wx.service.ITmpQrcodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huran
 * @since 2019-05-11
 */
@Api(tags = "二维码")
@Slf4j
@RestController
@RequestMapping("/rest/wx/qrcode")
public class QrcodeController extends Controller {
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;


    /**
     * 判断用户是否绑定二维码信息
     *
     * @param qrParam 二维码UUID
     * @param openId  用户标识
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "判断用户是否绑定二维码信息", notes = "判断用户是否绑定二维码信息")
    @GetMapping(value = "/bindingUserInfo")
    public ApiResponse isbindingUserInfo(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value =
            "openId") String openId) throws Exception {
        ApiResponse apiResponse = tmpQrcodeService.isbindingUserInfo(qrParam, openId);
        return apiResponse;
    }

    /**
     * 绑定用户信息 ---线下扫描的
     *
     * @param userInfoDTO   用户信息
     * @param bindingResult 校验用户信息
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "绑定用户信息", notes = "绑定用户信息")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping(value = "/bindingQr")
    public ApiResponse bindingQr(@RequestBody @Valid UserInfoDTO userInfoDTO, BindingResult bindingResult) throws
            Exception {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().stream().forEach(fieldError -> {
                log.info("==============>>>" + fieldError.getDefaultMessage());
                throw new BusinessException(ApiCode.EMPTY_PARAM, fieldError.getDefaultMessage());
            });
        }
        ApiResponse apiResponse = tmpQrcodeService.editOrSave(userInfoDTO);
        return apiResponse;
    }

    /**
     * 开启或者关闭通知
     *
     * @param qrParam  二维码UUID
     * @param openId   用户标识
     * @param isSwitch
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/onOrOffQr")
    public ApiResponse onOrOffQr(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value = "openId")
            String openId, @RequestParam(value = "isSwitch") String isSwitch) throws Exception {
        ApiResponse apiResponse = tmpQrcodeService.onOrOffQr(qrParam, openId, isSwitch);
        return apiResponse;
    }

    /**
     * 删除体验码
     *
     * @param qrParam 二维码UUID
     * @param openId  用户标识
     * @return
     * @throws Exception
     */
    @GetMapping(value = "deleteTmpQr")
    public ApiResponse deleteTmpQr(@RequestParam(value = "qrParam") String qrParam, @RequestParam(value = "openId")
            String openId) throws Exception {
        log.info("删除体验码参数:[{}],[{}]", qrParam, openId);
        ApiResponse apiResponse = tmpQrcodeService.deleteTmpQr(qrParam, openId);
        return apiResponse;

    }
}
