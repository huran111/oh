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
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

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
    private static int imageSize = 2000;

    final static String redisKey = "sharding:context:images";
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    CompletionService<Long> completionService = new ExecutorCompletionService<Long>(
            executor);

    @Override
    public void process(JobExecutionMultipleShardingContext shardingContext) {
        AtomicLong atomicLong = new AtomicLong(0);
        WxaConfig wxaConfig = new WxaConfig();
        wxaConfig.setAppId(wxProperties.getAppId());
        wxaConfig.setAppSecret(wxProperties.getAppSecret());
        WxaConfigKit.setWxaConfig(wxaConfig);
        String id = stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(id)) {
            log.info("任务开始.........");
            String dey = sdf.format(new Date());
            try {
                FileUtils.forceMkdir(new File("/home/images/qrParam/" + dey));
                WxaQrcodeApi wxaQrcodeApi1 = Duang.duang(WxaQrcodeApi.class);

                log.info("生成二维码到指定目录");
                for (int i = 0; i < imageSize; i++) {
                    try {
                        //生成二维码到指定目录
                        completionService.submit(new ImagesTask(wxaQrcodeApi1, dey, atomicLong));
                    } catch (Exception e) {
                        log.info("生成二维码到指定目录:[{}]", e.getCause());
                    }
                }
                for (int i = 0; i < imageSize; i++) {
                    try {
                        completionService.take().get();
                    } catch (Exception e) {
                        log.info("生成二维码到指定目录:[{}]", e.getCause());
                    }
                }
                log.info("任务完成,生成二二维码数量：[{}]", atomicLong.get());
                executor.shutdown();
                stringRedisTemplate.opsForValue().set(redisKey, "1", 1L, TimeUnit.MINUTES);
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();
            }
        } else {
            log.info("请稍后再试....");
        }

    }

}
