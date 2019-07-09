package com.tykj.aliyun.properties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * aliyun 通信参数
 */
@Data
@ConfigurationProperties(prefix = "aliyun")
@Component
public class AliYunProperties{
    private String regionId;
    private String accessKeyId;
    private String secret;
    private String domain;
    private String version;
    private String smsDomain;
    private String plDomain;
    private String signName;
    private String templateCode;
    private String poolKey;
    private String bindAxn;
    private String sendSms;
    private String addressKey;
    private String templateReserveCode;
    private String reserveCode;
}