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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
        return new ApiResponse(ApiCode.REQUEST_SUCCESS, loginSessionKeyDTO);
    }

    public static String wxDecrypt(String encrypted, String sessionKey, String iv) throws Exception {
        byte[] encrypData = Base64.decodeBase64(encrypted);
        byte[] ivData = Base64.decodeBase64(iv);
        byte[] sKey = Base64.decodeBase64(sessionKey);
        String decrypt = decrypt(sKey, ivData, encrypData);
        return decrypt;
    }

    public static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        //解析解密后的字符串
        return new String(cipher.doFinal(encData), "UTF-8");
    }

    /*
       * 获取 二维码图片
  　　 *
       */
    public static void getminiqrQr(String accessToken, String path) {
        try {
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // conn.setConnectTimeout(10000);//连接超时 单位毫秒
            // conn.setReadTimeout(2000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            paramJson.put("qrParam", UUIDUtils.getQrTmpUUID());
            paramJson.put("path", "pages/home/home");
            paramJson.put("width", 430);
            paramJson.put("is_hyaline", true);
            paramJson.put("auto_color", true);

            // line_color生效
            paramJson.put("auto_color", false);
            JSONObject lineColor = new JSONObject();
            lineColor.put("r", 0);
            lineColor.put("g", 0);
            lineColor.put("b", 0);
            paramJson.put("line_color", lineColor);
            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            OutputStream os = new FileOutputStream(new File(path));
            int len;
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1) {
                os.write(arr, 0, len);
                os.flush();
            }
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
