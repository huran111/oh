package com.tykj.msg;

import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * TODO：
 * </pre>
 *
 * @author BUCHU
 * @date 2019/6/6
 */
public abstract class SendTemplateMsg {
    public void sendTemplateMsg(String openId, String plate, String address, String token) {
        // 模板消息，发送测试：pass
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String message = "感谢您使用【欧海挪车】通知车主挪车，欧海挪车为广大车主提供更安全更便捷的智慧挪车服务";
        String remark = "点击申请我的挪车吗，畅享智慧车生活";
        if (null == address) {
            ApiResult result = TemplateMsgApi.send(TemplateData.New()
                    // 消息接收者
                    .setTouser(openId)
                    // 模板id
                    .setTemplate_id("QG4_bKOjuNdkWOIVqk8jRUykN0o9Y1lsP0YKH9KeGmw").setUrl("https://api.weixin.qq" +
                            "" + ".com/cgi-bin/message/wxopen/template/send?access_token=" + token + "")

                    // 模板参数
                    .add("keyword1", plate, "#000").add("keyword2", df.format(LocalDateTime.now()), "#333").add
                            ("keyword4", message, "#333").add("keyword5", remark).add("emphasis_keyword",
                            "keyword1.DATA").build());
        }
        ApiResult result = TemplateMsgApi.send(TemplateData.New()
                // 消息接收者
                .setTouser(openId)
                // 模板id
                .setTemplate_id("QG4_bKOjuNdkWOIVqk8jRYj0Z9XHJU84Ij5rxWp3_Qs").setUrl("https://api.weixin.qq" + "" +
                        ".com/cgi-bin/message/wxopen/template/send?access_token=" + token + "")

                // 模板参数
                .add("keyword1", plate, "#000").add("keyword2", df.format(LocalDateTime.now()), "#333").add
                        ("keyword3", address, "#333").add("keyword4", message, "#333").add("keyword5", remark).add
                        ("emphasis_keyword", "keyword1.DATA").build());

        System.out.println(result);
    }
}
