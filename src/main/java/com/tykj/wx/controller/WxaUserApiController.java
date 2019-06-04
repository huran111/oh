package com.tykj.wx.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.jfinal.aop.Duang;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaQrcodeApi;
import com.jfinal.wxaapp.api.WxaUserApi;
import com.jfinal.wxaapp.jfinal.WxaController;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.common.SysConstant;
import com.tykj.exception.BusinessException;
import com.tykj.utils.AESUtils;
import com.tykj.utils.WxUtils;
import com.tykj.wx.dto.AppIdDTO;
import com.tykj.wx.dto.LoginSessionKeyDTO;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.entity.User;
import com.tykj.wx.service.IQrcodeService;
import com.tykj.wx.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huran
 */
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping(value = "/rest/wx")
public class WxaUserApiController extends WxaController {


    WxaUserApi wxaUserApi = Duang.duang(WxaUserApi.class);

    @Autowired
    IUserService userService;
    @Autowired
    IQrcodeService qrcodeService;

    /**
     * 登陆接口
     */
    @GetMapping(value = "login")
    public ApiResponse login(HttpServletRequest request) throws Exception {
        setHttpServletRequest(request);
        String jsCode = getPara("code");
        if (StringUtils.isEmpty(jsCode)) {
            return new ApiResponse(ApiCode.EMPTY_PARAM);
        }
        // 获取SessionKey
        ApiResult apiResult = wxaUserApi.getSessionKey(jsCode);
        if (!apiResult.isSucceed()) {
            log.info(ApiCode.SESSION_KEY_FAIL.toString());
            throw new BusinessException(ApiCode.SESSION_KEY_FAIL, "获取session_key失败");
        }
        String sessionId = StrKit.getRandomUUID();
        // 利用 appId 与 accessToken 建立关联，支持多账户
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        //保持sessionID和openId到本地的CurrentHashMap中
        accessTokenCache.set(SysConstant.SESSION_KEY + sessionId, apiResult.getJson());
       // String signature = getPara("signature");
       // String rawData = getPara("rawData");
        String encryptedData = getPara("encryptedData");
        String iv = getPara("iv");
        // 参数空校验 不做演示
        // 利用 appId 与 accessToken 建立关联，支持多账户
       // IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
      //  String sessionId = getHeader(SysConstant.SESSION_KEY);
 /*       if (StrKit.isBlank(sessionId)) {
            return new ApiResponse(ApiCode.EMPTY_PARAM, "wxa_session Header is blank");
        }
        String sessionJson = accessTokenCache.get("wxa:session:" + sessionId);
        if (StrKit.isBlank(sessionJson)) {
            log.info("wxa_session sessionJson is blank");
            return new ApiResponse(ApiCode.EMPTY_PARAM, "wxa_session sessionJson is blank");
        }
        ApiResult sessionResult = ApiResult.create(sessionJson);
        // 获取sessionKey
        String sessionKey = sessionResult.get("session_key");
        if (StrKit.isBlank(sessionKey)) {
            log.info("sessionKey is blank");
            return new ApiResponse(ApiCode.EMPTY_PARAM, "sessionKey is blank");
        }*/
        // 用户信息校验
    /*    boolean check = wxaUserApi.checkUserInfo(sessionKey, rawData, signature);
        if (!check) {
            log.info("UserInfo check fail");
            return new ApiResponse(ApiCode.CHECK_FAIL, "UserInfo check fail");
        }*/
        String sessionKey = apiResult.get("session_key");
        String aaa= AESUtils.decrypt(encryptedData,sessionKey,iv,"UTF-8");
        // 服务端解密用户信息
        return ApiResponse.success();
    }
    /**
     * 服务端解密用户信息接口
     * 获取unionId
     */
    //@GetMapping(value = "/info")
    public ApiResponse info(HttpServletRequest request) {
        setHttpServletRequest(request);
        String signature = getPara("signature");
        String rawData = getPara("rawData");
        String encryptedData = getPara("encryptedData");
        String iv = getPara("iv");
        // 参数空校验 不做演示
        // 利用 appId 与 accessToken 建立关联，支持多账户
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String sessionId = getHeader(SysConstant.SESSION_KEY);
        if (StrKit.isBlank(sessionId)) {
            return new ApiResponse(ApiCode.EMPTY_PARAM, "wxa_session Header is blank");
        }
        String sessionJson = accessTokenCache.get("wxa:session:" + sessionId);
        if (StrKit.isBlank(sessionJson)) {
            log.info("wxa_session sessionJson is blank");
            return new ApiResponse(ApiCode.EMPTY_PARAM, "wxa_session sessionJson is blank");
        }
        ApiResult sessionResult = ApiResult.create(sessionJson);
        // 获取sessionKey
        String sessionKey = sessionResult.get("session_key");
        if (StrKit.isBlank(sessionKey)) {
            log.info("sessionKey is blank");
            return new ApiResponse(ApiCode.EMPTY_PARAM, "sessionKey is blank");
        }
        // 用户信息校验
        boolean check = wxaUserApi.checkUserInfo(sessionKey, rawData, signature);
        if (!check) {
            log.info("UserInfo check fail");
            return new ApiResponse(ApiCode.CHECK_FAIL, "UserInfo check fail");
        }
        // 服务端解密用户信息
        ApiResult apiResult = wxaUserApi.getUserInfo(sessionKey, encryptedData, iv);
        System.out.println(apiResult.toString());
        if (!apiResult.isSucceed()) {
            log.info("UserInfo decrypt fail");
            return new ApiResponse(ApiCode.CHECK_FAIL, "UserInfo decrypt fail");
        }
        System.out.println(apiResult.get("watermark").toString());
        //将用户信息保存
        User user = new User();
        user.setOpenId(apiResult.get("openId"))
                .setCreateTime(new Date())
                .setNickName(apiResult.get("nickName"))
                .setProvince(apiResult.get("province"))
                .setGender(apiResult.get("gender"))
                .setCity(apiResult.get("city"))
                .setAvatarUrl(apiResult.get("avatarUrl"))
                .setCountry(apiResult.get("country"));
        AppIdDTO appIdDTO = JSONObject.parseObject(apiResult.get("watermark").toString(), AppIdDTO.class);
        if (null != appIdDTO) {
            user.setId(appIdDTO.getAppid());
        }
        userService.saveOrUpdate(user);
        // 如果开发者拥有多个移动应用、网站应用、和公众帐号（包括小程序），可通过unionid来区分用户的唯一性
        // 同一用户，对同一个微信开放平台下的不同应用，unionid是相同的。
        //String unionId = apiResult.get("unionId");
        return new ApiResponse(ApiCode.OPERATOR_SUCCESS);
    }

    /**
     * 退出
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/logout")
    public ApiResponse loginOut(HttpServletRequest request) {
        setHttpServletRequest(request);
        String sessionId = getAttr(SysConstant.SESSION_KEY);
        if (org.apache.commons.lang3.StringUtils.isNoneEmpty(sessionId)) {
            IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
            accessTokenCache.remove(SysConstant.SESSION_KEY + sessionId);
            return new ApiResponse(ApiCode.LOGOUT_SUCCESS);
        }
        return new ApiResponse(ApiCode.ERROR);
    }


}
