package com.tykj.wx.dto;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * @author  huran
 */
@Data
@Accessors(chain = true)
public class UserInfoDTO {
    private String phone;
    private String platNum;
    private String code;
    private String qrParam;


}
