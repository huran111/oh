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
import com.tykj.aliyun.properties.AliYunProperties;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.common.SysConstant;
import com.tykj.utils.AESUtils;
import com.tykj.utils.LocationUtils;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.service.IQrcodeService;
import com.tykj.wx.service.ITmpQrcodeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
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
public class SendSms {

    @Resource
    private AliYunProperties aliYunProperties;
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;
    @Autowired
    private IQrcodeService qrcodeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/send")
    public ApiResponse getPhone(@RequestParam(value = "encryptedData") String encryptedData,
                                @RequestParam(value = "iv") String iv,
                                @RequestParam(value = "openId") String openId,
                                @RequestParam(value = "qrParam") String qrParam,
                                @RequestParam(value = "lat") String lat,
                                @RequestParam(value = "lng") String lng) throws Exception {
        String redisKey = String.format("expire:%s:%s", openId, qrParam);
        Long isKey = stringRedisTemplate.getExpire(redisKey);
        if (isKey > 0) {
            return new ApiResponse(ApiCode.REQUEST_SUCCESS, isKey);
        }
        String sessionKey = stringRedisTemplate.opsForValue().get("sessionKey:" + openId);
        stringRedisTemplate.opsForValue().set(redisKey, openId, 5, TimeUnit.MINUTES);
        //扫码人的手机号
        String saoPhone = "";
        if (StringUtils.isNotEmpty(sessionKey)) {
            String info = AESUtils.decrypt(encryptedData, sessionKey, iv, "UTF-8");
            saoPhone = (String) JSONObject.parseObject(info).get("phoneNumber");
        }
        DefaultProfile profile = DefaultProfile.getProfile(aliYunProperties.getRegionId(), aliYunProperties.getAccessKeyId(), aliYunProperties.getSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        String license = "";
        String noticePhone = "";
        if (SysConstant.TMP_QRPARAM.equals(qrParam)) {
            TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>().lambda()
                    .eq(TmpQrcode::getQrParam, qrParam));
            if (null != tmpQrcode) {
                if (SysConstant.SWITCH_0.equals(tmpQrcode.getIsSwitch())) {
                    return new ApiResponse(ApiCode.CLOSE_SWITCH);
                }
                license = tmpQrcode.getPlateNum();
                noticePhone = tmpQrcode.getPhoneNum();
            }
        } else {
            Qrcode qrcode = qrcodeService.getOne(new QueryWrapper<Qrcode>().lambda()
                    .eq(Qrcode::getQrParam, qrParam));

            if (null != qrcode) {
                if (SysConstant.SWITCH_0.equals(qrcode.getIsSwitch())) {
                    return new ApiResponse(ApiCode.CLOSE_SWITCH);
                }
                license = qrcode.getPlateNum();
                noticePhone = qrcode.getPhoneNum();

            }
        }
        Map<String, Object> map = LocationUtils.getLocation(lat, lng, aliYunProperties.getAddressKey());
        String address=String.format("%s,%s",map.get("city"),map.get("district"));
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
        //模板中变量
        request.putQueryParameter("TemplateParam", "{\"license\":\"" + license + "\",\"address\":\""+address+"\",\"phone\":\"" + saoPhone + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            return new ApiResponse(ApiCode.OPEN_SWITCH);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new ApiResponse(ApiCode.OPERATOR_FAIL);
    }
}
