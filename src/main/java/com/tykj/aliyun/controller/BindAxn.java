package com.tykj.aliyun.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jfinal.wxaapp.api.WxaAccessTokenApi;
import com.tykj.aliyun.dto.PhoneRootBean;
import com.tykj.aliyun.properties.AliYunProperties;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.common.SysConstant;
import com.tykj.listener.SysDeleteData;
import com.tykj.msg.SendTemplateMsg;
import com.tykj.utils.DateUtils;
import com.tykj.utils.UUIDUtils;
import com.tykj.wx.entity.Qrcode;
import com.tykj.wx.entity.QrcodeRecord;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.entity.TmpqrcodeRecord;
import com.tykj.wx.service.IQrcodeRecordService;
import com.tykj.wx.service.IQrcodeService;
import com.tykj.wx.service.ITmpQrcodeService;
import com.tykj.wx.service.ITmpqrcodeRecordService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @program: tykj-system
 * @description: 绑定手机号
 * @author: Mr.Zhang
 * @create: 2019-05-17 16:47
 **/
@Slf4j
@RestController
@RequestMapping("/rest/wx/bindaxn")
public class BindAxn extends SendTemplateMsg {

    @Resource
    private AliYunProperties aliYunProperties;
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;
    @Autowired
    private IQrcodeService qrcodeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ITmpqrcodeRecordService tmpqrcodeRecordService;
    @Autowired
    private IQrcodeRecordService qrcodeRecordService;

    @RequestMapping("/getphonex")
    public ApiResponse getPhone(@RequestParam(value = "id") String id, @RequestParam(value = "qrParam") String
            qrParam, @RequestParam(value = "formId", required = false) String formId) throws Exception {
        log.info("打电话:[{}],[{}]", id, qrParam);
        DefaultProfile profile = DefaultProfile.getProfile(aliYunProperties.getRegionId(), aliYunProperties
                .getAccessKeyId(), aliYunProperties.getSecret());
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();
        String openId = "";
        String plate = "";
        if (qrParam.contains(SysConstant.TMP_QRPARAM)) {
            TmpQrcode tmpQrcode = tmpQrcodeService.getOne(new QueryWrapper<TmpQrcode>().lambda().eq(TmpQrcode::getId,
                    id).eq(TmpQrcode::getQrParam, qrParam));
            if (SysConstant.SWITCH_0.equals(tmpQrcode.getIsSwitch())) {
                return new ApiResponse(ApiCode.CLOSE_SWITCH);
            }
            if (null != tmpQrcode) {
                openId = tmpQrcode.getOpenId();
                plate = tmpQrcode.getPlateNum();
                request.putQueryParameter("PhoneNoA", tmpQrcode.getPhoneNum());
                String finalOpenId = openId;
                String finalPlate = plate;
                try {
                    new Thread(() -> {
                        super.sendTemplateMsg(finalOpenId, finalPlate, "暂无", WxaAccessTokenApi.getAccessTokenStr(),
                                formId);
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            Qrcode qrcode = qrcodeService.getOne(new QueryWrapper<Qrcode>().lambda().eq(Qrcode::getId, id).eq
                    (Qrcode::getQrParam, qrParam));
            if (SysConstant.SWITCH_0.equals(qrcode.getIsSwitch())) {
                return new ApiResponse(ApiCode.CLOSE_SWITCH);
            }
            if (null != qrcode) {
                request.putQueryParameter("PhoneNoA", qrcode.getPhoneNum());
                openId = qrcode.getOpenId();
                plate = qrcode.getPlateNum();
                String finalOpenId = openId;
                String finalPlate = plate;
                try {
                    new Thread(() -> {
                        super.sendTemplateMsg(finalOpenId, finalPlate, "暂无", WxaAccessTokenApi.getAccessTokenStr(),
                                formId);
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        //存储虚拟机号码
        String phoneInfo = stringRedisTemplate.opsForValue().get(id);
        log.info("redis-存储虚拟机号码:[{}]", phoneInfo);
        if (StringUtils.isNotEmpty(phoneInfo)) {
            PhoneRootBean phoneRootBean = JSONObject.parseObject(phoneInfo, PhoneRootBean.class);
            String phone = phoneRootBean.getSecretBindDTO().getSecretNo();
            return new ApiResponse(ApiCode.OPEN_SWITCH, phone);
        }
        //request.setProtocol(ProtocolType.HTTPS);
        request.setMethod(MethodType.POST);
        request.setDomain(aliYunProperties.getPlDomain());
        request.setVersion(aliYunProperties.getVersion());
        request.setAction(aliYunProperties.getBindAxn());
        request.putQueryParameter("PoolKey", aliYunProperties.getPoolKey());
        request.putQueryParameter("Expiration", DateUtils.addSeconds());
        CommonResponse response = client.getCommonResponse(request);
        log.info("============>绑定号码状态" + response.getData());
        PhoneRootBean phoneRootBean = JSONObject.parseObject(response.getData(), PhoneRootBean.class);
        stringRedisTemplate.opsForValue().set(id, JSONObject.toJSONString(phoneRootBean), 10, TimeUnit.MINUTES);
        String phone = phoneRootBean.getSecretBindDTO().getSecretNo();
        if (qrParam.contains(SysConstant.TMP_QRPARAM)) {
            try {
                new Thread(() -> {
                    SysDeleteData.tmpRecord.add(qrParam);
                    TmpqrcodeRecord record = new TmpqrcodeRecord();
                    record.setId(UUIDUtils.getUUID())
                            .setCreateTime(new Date()).setPhone(phone).setQrParam(qrParam).setFlag("2");
                    if(response.getHttpStatus()==200){
                        record.setStatus("1");
                    }else {
                        record.setStatus("2");
                    }
                    tmpqrcodeRecordService.save(record);
                }).start();
            } catch (Exception e) {
                log.info("保存临时二维码记录:[{}]", e.getCause());
            }
        } else {
            try {
                new Thread(() -> {
                    QrcodeRecord record = new QrcodeRecord();
                    record.setId(UUIDUtils.getUUID())
                            .setCreateTime(new Date()).setPhone(phone).setQrParam(qrParam).setFlag("2");
                    if(response.getHttpStatus()==200){
                        record.setStatus("1");
                    }else {
                        record.setStatus("2");
                    }
                    qrcodeRecordService.save(record);
                }).start();
            } catch (Exception e) {
                log.info("保存二维码记录:[{}]", e.getCause());
            }
        }

        return new ApiResponse(ApiCode.OPEN_SWITCH, phone);
    }
}

