package com.tykj.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Duang;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.wxaapp.api.WxaUserApi;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.exception.BusinessException;
import com.tykj.wx.dto.LoginSessionKeyDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @auther huran
 * @date
 **/
@Slf4j
public class WxUtils {
    private static WxaUserApi wxaUserApi = Duang.duang(WxaUserApi.class);

    public static ApiResponse<LoginSessionKeyDTO> getOpenId(String jsCode) {
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
        return  new ApiResponse(ApiCode.REQUEST_SUCCESS,loginSessionKeyDTO);
    }
}
