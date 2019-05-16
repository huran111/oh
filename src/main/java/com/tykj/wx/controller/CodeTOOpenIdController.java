package com.tykj.wx.controller;

import com.tykj.common.ApiCode;
import com.tykj.common.ApiResponse;
import com.tykj.utils.WxUtils;
import com.tykj.wx.dto.LoginSessionKeyDTO;
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
    @GetMapping(value = "/codeToOpenId")
    public ApiResponse codeToOpenId(@RequestParam(value = "code") String code)  throws Exception{
        LoginSessionKeyDTO loginSessionKeyDTO = WxUtils.getOpenId(code).getData();
        return new ApiResponse(ApiCode.REQUEST_SUCCESS, loginSessionKeyDTO);
    }
}
