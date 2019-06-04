package com.tykj.aliyun.dto;

import lombok.Data;

/**
 * @Description TODO
 * @auther huran
 * @date
 **/
@Data
public class SecretBindDTO {
    private String Extension;
    private String SecretNo;
    private String SubsId;
    public void setExtension(String Extension) {
        this.Extension = Extension;
    }
}
