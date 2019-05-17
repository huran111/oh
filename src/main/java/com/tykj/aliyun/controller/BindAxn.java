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
import java.time.LocalDateTime;

/**
 * @program: tykj-system
 * @description: 绑定手机号
 * @author: Mr.Zhang
 * @create: 2019-05-17 16:47
 **/
@Slf4j
@RestController
@RequestMapping("/rest/bindaxn")
public class BindAxn {

	@Resource
	private AliYunProperties aliYunProperties;
	@RequestMapping("/getphonex")
	public ApiResponse getPhone(@RequestParam(value = "openId") String openId) {
		DefaultProfile profile = DefaultProfile.getProfile(aliYunProperties.getRegionId(), aliYunProperties.getAccessKeyId(), aliYunProperties.getSecret());
		IAcsClient client = new DefaultAcsClient(profile);

		CommonRequest request = new CommonRequest();
		//request.setProtocol(ProtocolType.HTTPS);
		request.setMethod(MethodType.POST);
		request.setDomain(aliYunProperties.getPlDomain());
		request.setVersion(aliYunProperties.getVersion());
		request.setAction(aliYunProperties.getBindAxn());
		request.putQueryParameter("PoolKey", aliYunProperties.getPoolKey());
		request.putQueryParameter("Expiration", "2019-05-17 20:43:00");
		request.putQueryParameter("PhoneNoA", "18539469868");
		try {
			CommonResponse response = client.getCommonResponse(request);

			log.info("============>绑定号码状态"+response.getData());
			return new ApiResponse(ApiCode.REQUEST_SUCCESS, response.getData());
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return new ApiResponse(ApiCode.OPERATOR_FAIL);
	}
}
