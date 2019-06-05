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
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.commons.codec.binary.Base64;

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

    public static String wxDecrypt (String encrypted, String sessionKey, String iv)throws Exception {
        byte[] encrypData = Base64.decodeBase64(encrypted);
        byte[] ivData = Base64.decodeBase64(iv);
        byte[] sKey = Base64.decodeBase64(sessionKey);
        String decrypt = decrypt(sKey,ivData,encrypData);
        return decrypt;
    }

    public static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        //解析解密后的字符串
        return new String(cipher.doFinal(encData),"UTF-8");
    }


}
