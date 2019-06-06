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
    public ApiResponse getPhone(@RequestParam(value = "encryptedData") String encryptedData, @RequestParam(value =
            "iv") String iv, @RequestParam(value = "openId") String openId, @RequestParam(value = "qrParam") String
            qrParam, @RequestParam(value = "lat") String lat, @RequestParam(value = "lng") String lng) throws
            Exception {
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
        DefaultProfile profile = DefaultProfile.getProfile(aliYunProperties.getRegionId(), aliYunProperties
                .getAccessKeyId(), aliYunProperties.getSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        String license = "";
        String noticePhone = "";
        String plate = "";
        if (SysConstant.TMP_QRPARAM.equals(qrParam)) {
            TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>().lambda().eq
                    (TmpQrcode::getQrParam, qrParam));
            if (null != tmpQrcode) {
                plate = tmpQrcode.getPlateNum();
                if (SysConstant.SWITCH_0.equals(tmpQrcode.getIsSwitch())) {
                    return new ApiResponse(ApiCode.CLOSE_SWITCH);
                }
                license = tmpQrcode.getPlateNum();
                noticePhone = tmpQrcode.getPhoneNum();
            }
        } else {
            Qrcode qrcode = qrcodeService.getOne(new QueryWrapper<Qrcode>().lambda().eq(Qrcode::getQrParam, qrParam));

            if (null != qrcode) {
                plate = qrcode.getPlateNum();
                if (SysConstant.SWITCH_0.equals(qrcode.getIsSwitch())) {
                    return new ApiResponse(ApiCode.CLOSE_SWITCH);
                }
                license = qrcode.getPlateNum();
                noticePhone = qrcode.getPhoneNum();

            }
        }
        Map<String, Object> map = LocationUtils.getLocation(lat, lng, aliYunProperties.getAddressKey());
        String address = String.format("%s,%s", map.get("city"), map.get("district"));
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
        request.putQueryParameter("TemplateParam", "{\"license\":\"" + license + "\",\"address\":\"" + address + "\"," +
                "" + "\"phone\":\"" + saoPhone + "\"}");
        String finalPlate = plate;
        new Thread(() -> {
            templateMsg(openId, finalPlate, address);
        }).start();
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

    /**
     * 推送消息到微信
     *
     * @return
     */
    public String templateMsg(String openId, String plate, String address) {
        // 模板消息，发送测试：pass
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String message = "感谢您使用【欧海挪车】通知车主挪车，欧海挪车为广大车主提供更安全更便捷的智慧挪车服务";
        String remark = "点击申请我的挪车吗，畅享智慧车生活";

        ApiResult result = TemplateMsgApi.send(TemplateData.New()
                // 消息接收者
                .setTouser(openId)
                // 模板id
                .setTemplate_id("QG4_bKOjuNdkWOIVqk8jRYj0Z9XHJU84Ij5rxWp3_Qs").setUrl("https://api.weixin.qq" + "" +
                        ".com/cgi-bin/message/wxopen/template/send")

                // 模板参数
                .add("keyword1", plate, "#000").add("keyword2", df.format(LocalDateTime.now()), "#333").add
                        ("keyword3", address, "#333").add("keyword4", message, "#333").add("keyword5", remark).add
                        ("emphasis_keyword", "keyword1.DATA").build());
        System.out.println(result);
        return null;
    }
}
