package com.tykj.wx.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoDTO {
    private String phone;
    private String platNum;
    private String code;
    private String qrParam;


}
