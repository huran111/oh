package com.tykj.wx.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaUserApi;
import com.jfinal.wxaapp.jfinal.WxaController;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.wx.dto.LoginSessionKeyDTO;
import com.tykj.wx.entity.User;
import com.tykj.wx.properties.WxProperties;
import com.tykj.wx.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@RestController
@RequestMapping(value = "wx")
public class WxaUserApiController extends WxaController {

    @Autowired
    WxProperties wxProperties;
    WxaUserApi wxaUserApi = Duang.duang(WxaUserApi.class);
    static final WxaConfig wxaConfig = new WxaConfig();
    static final ApiConfig apiConfigKit = new ApiConfig();
    @Autowired
    IUserService userService;


    /**
     * 登陆接口
     */
    @Transactional
    @GetMapping(value = "login")
    public ApiResponse login(HttpServletRequest request) {
        try {
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
            // 返回{"session_key":"nzoqhc3OnwHzeTxJs+inbQ==","expires_in":2592000,"openid":"oVBkZ0aYgDMDIywRdgPW8-joxXc4"}
            if (!apiResult.isSucceed()) {
                return new ApiResponse(ApiCode.SESSION_KEY_FAIL);
            }
            // 利用 appId 与 accessToken 建立关联，支持多账户
            IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
            String sessionId = StrKit.getRandomUUID();

            //保持sessionID和openId到本地的CurrentHashMap中
            accessTokenCache.set("wxa:session:" + sessionId, apiResult.getJson());
            LoginSessionKeyDTO dto = JSONObject.parseObject(apiResult.getJson(), LoginSessionKeyDTO.class);
            if (null != dto) {
                //设置sessionId
                dto.setSessionID(sessionId);
                User user = new User();
                user.setId(StrKit.getRandomUUID())
                        .setOpenId(dto.getOpenid()).setCreateTime(new Date());
                return new ApiResponse(ApiCode.LONG_SUCCESS, dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(ApiCode.ERROR);
        }
        return null;
    }
    /**
     * 服务端解密用户信息接口
     * 获取unionId
     */
    @GetMapping(value = "/info")
    public ApiResponse info(HttpServletRequest request) {
        setHttpServletRequest(request);
        String signature = getPara("signature");
        String rawData = getPara("rawData");
        String encryptedData = getPara("encryptedData");
        String iv = getPara("iv");

        // 参数空校验 不做演示
        // 利用 appId 与 accessToken 建立关联，支持多账户
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String sessionId = getHeader("wxa-sessionid");
        if (StrKit.isBlank(sessionId)) {
            return new ApiResponse(ApiCode.EMPTY_PARAM,"wxa_session Header is blank");
        }
        String sessionJson = accessTokenCache.get("wxa:session:" + sessionId);
        if (StrKit.isBlank(sessionJson)) {
            return new ApiResponse(ApiCode.EMPTY_PARAM,"wxa_session sessionJson is blank");
        }
        ApiResult sessionResult = ApiResult.create(sessionJson);
        // 获取sessionKey
        String sessionKey = sessionResult.get("session_key");
        if (StrKit.isBlank(sessionKey)) {
            return new ApiResponse(ApiCode.EMPTY_PARAM,"sessionKey is blank");
        }
        // 用户信息校验
        boolean check = wxaUserApi.checkUserInfo(sessionKey, rawData, signature);
        if (!check) {
            return new ApiResponse(ApiCode.CHECK_FAIL,"UserInfo check fail");
        }
        // 服务端解密用户信息
        ApiResult apiResult = wxaUserApi.getUserInfo(sessionKey, encryptedData, iv);
        System.out.println(apiResult.toString());
        if (!apiResult.isSucceed()) {
            return new ApiResponse(ApiCode.CHECK_FAIL,"UserInfo decrypt fail");
        }
        // 如果开发者拥有多个移动应用、网站应用、和公众帐号（包括小程序），可通过unionid来区分用户的唯一性
        // 同一用户，对同一个微信开放平台下的不同应用，unionid是相同的。
        //String unionId = apiResult.get("unionId");
        return   new ApiResponse(ApiCode.SUCCESS);
    }
}
