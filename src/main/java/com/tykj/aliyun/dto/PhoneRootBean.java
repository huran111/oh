package com.tykj.aliyun.dto;

import lombok.Data;

/**
 * @Description TODO
 * @auther huran
 * @date
 **/
@Data
public class PhoneRootBean {
    private String Message;
    private String RequestId;
    private String Code;
    private SecretBindDTO SecretBindDTO;
}
