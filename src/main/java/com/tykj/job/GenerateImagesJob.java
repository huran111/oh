package com.tykj.job;/**
 * <pre>
 * TODO：
 * </pre>
 *
 * @author BUCHU
 * @date 2019/6/10
 */

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.jfinal.aop.Duang;
import com.jfinal.weixin.sdk.utils.IOUtils;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaQrcodeApi;
import com.tykj.utils.UUIDUtils;
import com.tykj.wx.properties.WxProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡冉
 * @ClassName generateImagesJob
 * @Date 2019/6/10 12:47
 * @Version 2.0
 */
@Slf4j
@Component
public class GenerateImagesJob extends AbstractSimpleElasticJob {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    WxProperties wxProperties;
    final static String redisKey = "sharding:context:images";
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public void process(JobExecutionMultipleShardingContext shardingContext) {
        WxaConfig wxaConfig = new WxaConfig();
        wxaConfig.setAppId(wxProperties.getAppId());
        wxaConfig.setAppSecret(wxProperties.getAppSecret());
        WxaConfigKit.setWxaConfig(wxaConfig);
        String id = null;/*stringRedisTemplate.opsForValue().get(redisKey);*/

        if (StringUtils.isEmpty(id)) {
            log.info("任务开始.........");
            String dey = sdf.format(new Date());
            try {
                FileUtils.forceMkdir(new File("/home/images/qrParam/" + dey));
                WxaQrcodeApi wxaQrcodeApi1 = Duang.duang(WxaQrcodeApi.class);
                for (int i = 0; i < 100; i++) {
                    //生成二维码到指定目录
                    log.info("生成二维码到指定目录");
                    String qrParamId = UUIDUtils.getUUID();
                    InputStream inputStream = wxaQrcodeApi1.getUnLimit(qrParamId, "pages/home/home");
                    IOUtils.toFile(inputStream, new File("/home/images/qrParam/" + dey + "/" + qrParamId + ".png"));
                }
            //    stringRedisTemplate.opsForValue().set(redisKey, "1", 5L, TimeUnit.MINUTES);
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();
            }
        }else {
            log.info("请稍后再试....");
        }

    }

}
