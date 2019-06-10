package com.tykj.wx.dto;

import com.tykj.common.SysConstant;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author  huran
 */
@Data
@Accessors(chain = true)
public class UserInfoDTO {
    @NotNull(message = "手机号不能为空")
    private String phone;
    @NotNull(message = "车牌号不能为空")
    private String platNum;
    @NotNull(message = "openId不能为空")
    private String openId;
    private String qrParam;
    private String id;


}
