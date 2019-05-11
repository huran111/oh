package com.tykj.wx.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfinal.aop.Duang;
import com.jfinal.core.Controller;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaUserApi;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import com.tykj.wx.dto.LoginSessionKeyDTO;
import com.tykj.wx.dto.UserInfoDTO;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.properties.WxProperties;
import com.tykj.wx.service.IQrcodeService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author huran
 * @since 2019-05-11
 */
@Slf4j
@RestController
@RequestMapping("/wx/qrcode")
public class QrcodeController extends Controller {
    @Autowired
    IQrcodeService qrcodeService;
    static final WxaConfig wxaConfig = new WxaConfig();
    @Autowired
    private WxProperties wxProperties;
    private WxaUserApi wxaUserApi = Duang.duang(WxaUserApi.class);

    /**
     * 判断用户是否绑定二维码信息
     *
     * @param qrParam
     * @return
     */
    @GetMapping(value = "/bindingUserInfo")
    public ApiResponse isbindingUserInfo(@RequestParam(value = "qrParam") String qrParam) throws Exception {
        QueryWrapper<Qrcode> queryWrapper = new QueryWrapper<Qrcode>();
        queryWrapper.lambda().eq(Qrcode::getQrParam, qrParam).eq(Qrcode::getIsBinding, "1");
        Qrcode qrcode = qrcodeService.getOne(queryWrapper);
        if (null != qrcode) {
            //已经绑定
            return new ApiResponse(ApiCode.BINDING, qrcode);
        }
        //未绑定
        return new ApiResponse(ApiCode.NOT_BINDING);
    }

    /**
     * 绑定用户信息
     *
     * @return
     */
    @PostMapping(value = "/bindingQr")
    public ApiResponse bindingQr(UserInfoDTO userInfoDTO, HttpServletRequest request) throws Exception {
        setHttpServletRequest(request);
        wxaConfig.setAppId(wxProperties.getAppId());
        wxaConfig.setAppSecret(wxProperties.getAppSecret());
        WxaConfigKit.setWxaConfig(wxaConfig);
        String jsCode = getPara("code");
        if (StringUtils.isEmpty(jsCode)) {
            return new ApiResponse(ApiCode.EMPTY_PARAM);
        }
        // 获取SessionKey
        ApiResult apiResult = wxaUserApi.getSessionKey(jsCode);
        if (!apiResult.isSucceed()) {
            log.info(ApiCode.SESSION_KEY_FAIL.toString());
            return new ApiResponse(ApiCode.SESSION_KEY_FAIL);
        }
        LoginSessionKeyDTO loginSessionKeyDTO = JSONObject.parseObject(apiResult.getJson(), LoginSessionKeyDTO.class);
        String sessionKey = loginSessionKeyDTO.getSession_key();
        if (StringUtils.isEmpty(sessionKey)) {
            throw new BusinessException(ApiCode.EMPTY_PARAM, "sessionKey is null");
        }
        Qrcode qrcode = new Qrcode();
        qrcode.setId(loginSessionKeyDTO.getOpenid())
                .setPlateNum(userInfoDTO.getPhone()).setPlateNum(userInfoDTO.getPlatNum())
                .setQrParam(userInfoDTO.getQrParam()).setCreateTime(new Date());
        qrcodeService.saveOrUpdate(qrcode);
        return new ApiResponse(ApiCode.BINDING);
    }
}
