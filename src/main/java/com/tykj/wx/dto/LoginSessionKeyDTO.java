package com.tykj.wx.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;
/**
 * @author  huran
 */
@Data
@Accessors(chain = true)
public class LoginSessionKeyDTO {
    @JSONField(serialize = false)
    private String session_key;
    private String expires_in;
    @JSONField(serialize = false)
    private String openid;
    private String sessionID;


}
