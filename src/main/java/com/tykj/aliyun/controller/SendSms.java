package com.tykj.aliyun.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.tykj.aliyun.properties.AliYunProperties;
import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @program: tykj-system
 * @description: 短信通知
 * @author: Mr.Zhang
 * @create: 2019-05-17 16:42
 **/
@Slf4j
@RestController
@RequestMapping("/rest/sms")
public class SendSms {

	@Resource
	private AliYunProperties aliYunProperties;
	@RequestMapping("/send")
	public ApiResponse getPhone(@RequestParam(value = "openId") String openId) {
		DefaultProfile profile = DefaultProfile.getProfile(aliYunProperties.getRegionId(), aliYunProperties.getAccessKeyId(), aliYunProperties.getSecret());
		IAcsClient client = new DefaultAcsClient(profile);

			CommonRequest request = new CommonRequest();
			//request.setProtocol(ProtocolType.HTTPS);
			request.setMethod(MethodType.POST);
			request.setDomain(aliYunProperties.getSmsDomain());
			request.setVersion(aliYunProperties.getVersion());
			request.setAction(aliYunProperties.getSendSms());
			request.putQueryParameter("SignName", aliYunProperties.getSignName());
			//短信模板
			request.putQueryParameter("TemplateCode", aliYunProperties.getTemplateCode());
			request.putQueryParameter("PhoneNumbers", "18539469868");
			//模板中变量
			request.putQueryParameter("TemplateParam", "{\"license\":\"豫A66666\",\"address\":\"郑州市\",\"phone\":\"18539469868\"}");
			try {
				CommonResponse response = client.getCommonResponse(request);
				System.out.println(response.getData());
				return new ApiResponse(ApiCode.REQUEST_SUCCESS, response.getData());
			} catch (ServerException e) {
				e.printStackTrace();
			} catch (ClientException e) {
				e.printStackTrace();
		}
		return new ApiResponse(ApiCode.OPERATOR_FAIL);
	}
}
