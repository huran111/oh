package com.tykj.msg;

import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.wxaapp.api.WxaTemplate;
import com.jfinal.wxaapp.api.WxaTemplateApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
@Slf4j
public abstract class SendTemplateMsg {
    public void sendTemplateMsg(String openId, String plate, String address, String token, String fromId) {
        log.info("发送模板消息:openId:[{}] plate:[{}] address:[{}],token:[{}],fromId:[{}]", openId, plate, address, token,
                fromId);
        try {
            // 模板消息，发送测试：pass
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String message = "感谢您使用【欧海挪车】通知车主挪车，欧海挪车为广大车主提供更安全更便捷的智慧挪车服务";
            String remark = "点击申请我的挪车吗，畅享智慧车生活";
            if ("暂无".equalsIgnoreCase(address)) {
                WxaTemplateApi wxaTemplateApi = new WxaTemplateApi();
                WxaTemplate wxaTemplate = new WxaTemplate();
                wxaTemplate.setTemplate_id("QG4_bKOjuNdkWOIVqk8jRUykN0o9Y1lsP0YKH9KeGmw");
                wxaTemplate.setEmphasis_keyword("keyword1.DATA").setTouser(openId).setForm_id(fromId).add("keyword1",
                        plate, "000").add("keyword2", df.format(LocalDateTime.now()), "#333").add("keyword4",
                        message, "#333").add("keyword5", remark);
                ApiResult apiResult = wxaTemplateApi.send(wxaTemplate);
                log.info("是否成功:[{}],数据:[{}]", apiResult.isSucceed(), apiResult.getJson());

            } else {
                WxaTemplateApi wxaTemplateApi = new WxaTemplateApi();
                WxaTemplate wxaTemplate = new WxaTemplate();
                wxaTemplate.setTemplate_id("QG4_bKOjuNdkWOIVqk8jRUykN0o9Y1lsP0YKH9KeGmw");
                wxaTemplate.setEmphasis_keyword("keyword1.DATA").setTouser(openId).setForm_id(fromId).add("keyword1",
                        plate, "000").add("keyword2", df.format(LocalDateTime.now()), "#333").add("keyword3",
                        address, "#333").add("keyword4", message, "#333").add("keyword5", remark);
                ApiResult apiResult = wxaTemplateApi.send(wxaTemplate);
                log.info("是否成功:[{}],数据:[{}]", apiResult.isSucceed(), apiResult.getJson());
            }
        } catch (Exception e) {
            log.info("异常：[{}]", e.getCause());
        }


    }
}
