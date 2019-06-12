package com.tykj.aliyun.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.wxaapp.api.WxaAccessTokenApi;
import com.tykj.aliyun.properties.AliYunProperties;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.common.SysConstant;
import com.tykj.msg.SendTemplateMsg;
import com.tykj.utils.AESUtils;
import com.tykj.utils.LocationUtils;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.service.IQrcodeService;
import com.tykj.wx.service.ITmpQrcodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: tykj-system
 * @description: 短信通知
 * @author: Mr.Zhang
 * @create: 2019-05-17 16:42
 **/
@Slf4j
@RestController
@RequestMapping("/rest/wx/sms")
public class SendSms extends SendTemplateMsg {

    @Resource
    private AliYunProperties aliYunProperties;
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;
    @Autowired
    private IQrcodeService qrcodeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/send")
    public ApiResponse getPhone(@RequestParam(value = "encryptedData") String encryptedData, @RequestParam(value =
            "iv") String iv, @RequestParam(value = "openId") String openId, @RequestParam(value = "qrParam") String
                                        qrParam, @RequestParam(value = "lat") String lat, @RequestParam(value = "lng") String lng
            , @RequestParam(value = "fromId",required = false) String fromId) throws
            Exception {
        log.info("发送短信开始: openId:[{}],lat:[{}],lng:[{}]", openId, lat, lng);
        try {
            String sessionKey = stringRedisTemplate.opsForValue().get("sessionKey:" + openId);
            //扫码人的手机号
            String saoPhone = "";
            if (StringUtils.isNotEmpty(sessionKey)) {
                String info = AESUtils.decrypt(encryptedData, sessionKey, iv, "UTF-8");
                saoPhone = (String) JSONObject.parseObject(info).get("phoneNumber");
            } else {
                return ApiResponse.error();
            }
            DefaultProfile profile = DefaultProfile.getProfile(aliYunProperties.getRegionId(), aliYunProperties
                    .getAccessKeyId(), aliYunProperties.getSecret());
            IAcsClient client = new DefaultAcsClient(profile);
            String license = "";
            String noticePhone = "";
            String plate = "";
            if (qrParam.contains(SysConstant.TMP_QRPARAM)) {
                TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>().lambda().eq
                        (TmpQrcode::getQrParam, qrParam));
                if (null != tmpQrcode) {
                    plate = tmpQrcode.getPlateNum();
                    if (SysConstant.SWITCH_0.equals(tmpQrcode.getIsSwitch())) {
                        return new ApiResponse(ApiCode.CLOSE_SWITCH);
                    }
                    license = tmpQrcode.getPlateNum();
                    noticePhone = tmpQrcode.getPhoneNum();
                } else {
                    return ApiResponse.error();
                }
            } else {
                Qrcode qrcode = qrcodeService.getOne(new QueryWrapper<Qrcode>().lambda().eq(Qrcode::getQrParam,
                        qrParam));
                if (null != qrcode) {
                    plate = qrcode.getPlateNum();
                    if (SysConstant.SWITCH_0.equals(qrcode.getIsSwitch())) {
                        return new ApiResponse(ApiCode.CLOSE_SWITCH);
                    }
                    license = qrcode.getPlateNum();
                    noticePhone = qrcode.getPhoneNum();
                } else {
                    return ApiResponse.error();
                }
            }
            Map<String, Object> map = LocationUtils.getLocation(lat, lng);
            String address = String.format("%s,%s", map.get("city"), map.get("district"));
            log.info("地址:[{}]", address);
            CommonRequest request = new CommonRequest();
            //request.setProtocol(ProtocolType.HTTPS);
            request.setMethod(MethodType.POST);
            request.setDomain(aliYunProperties.getSmsDomain());
            request.setVersion(aliYunProperties.getVersion());
            request.setAction(aliYunProperties.getSendSms());
            request.putQueryParameter("SignName", aliYunProperties.getSignName());
            //短信模板
            request.putQueryParameter("TemplateCode", aliYunProperties.getTemplateCode());
            request.putQueryParameter("PhoneNumbers", noticePhone);
            log.info("通知的手机号:[{}],扫码的手机号：[{}]", noticePhone, saoPhone);
            //模板中变量
            request.putQueryParameter("TemplateParam", "{\"license\":\"" + license + "\",\"address\":\"" + address +
                    "\"," + "" + "" + "\"phone\":\"" + saoPhone + "\"}");
            String finalPlate = plate;
         /*   new Thread(() -> {
                super.sendTemplateMsg(openId, finalPlate, address, WxaAccessTokenApi.getAccessTokenStr(),fromId);
            }).start();*/
            try {
                CommonResponse response = client.getCommonResponse(request);
                log.info("发送短信通知状态:[{}],[{}]", response.getHttpStatus(), response.getData());
                return new ApiResponse(ApiCode.OPEN_SWITCH);
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            return new ApiResponse(ApiCode.OPERATOR_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ApiResponse.success();
    }


}
