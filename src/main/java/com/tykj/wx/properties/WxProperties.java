package com.tykj.wx.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "wx")
@Component
public class WxProperties {
     private String appId;
     private String appSecret;
}
