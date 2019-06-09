package com.tykj.wx.controller;

import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.utils.WxUtils;
import com.tykj.wx.dto.LoginSessionKeyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description code转OPENID
 * @auther huran
 * @date
 **/
@RestController
@RequestMapping(value = "/rest/wx/cto")
public class CodeTOOpenIdController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping(value = "/codeToOpenId")
    public ApiResponse codeToOpenId(@RequestParam(value = "code") String code)  throws Exception{
        LoginSessionKeyDTO loginSessionKeyDTO = WxUtils.getOpenId(code).getData();
        stringRedisTemplate.opsForValue().set(String.format("%s:%s","sessionKey",loginSessionKeyDTO.getOpenid()),loginSessionKeyDTO.getSession_key());
        return new ApiResponse(ApiCode.REQUEST_SUCCESS, loginSessionKeyDTO);
    }
}
