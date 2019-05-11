package com.tykj.wx.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AppIdDTO {
    private  String appid;
    private Long timestamp;
}
