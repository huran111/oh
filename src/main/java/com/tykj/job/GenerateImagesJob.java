package com.tykj.job;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.google.common.collect.Lists;
import com.jfinal.aop.Duang;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.jfinal.wxaapp.api.WxaQrcodeApi;
import com.tykj.utils.UUIDUtils;
import com.tykj.wx.entity.JobParamRecord;
import com.tykj.wx.properties.WxProperties;
import com.tykj.wx.service.IJobParamRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private WxProperties wxProperties;
    @Autowired
    private IJobParamRecordService jobParamRecordService;
    private static int imageSize = 10;
    //保存生成的二维码路径
    private List<String> tempList = Lists.newCopyOnWriteArrayList();
    final static String redisKey = "sharding:context:images";
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private CompletionService<List> completionService = new ExecutorCompletionService<List>(executor);

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
                //生成二维码到指定目录
                this.generateQrcode(imageSize, completionService, wxaQrcodeApi1, dey, atomicLong);
                //等待线程执行完毕
                tempList = this.waitGenerateQrcode(imageSize, completionService);
                if (CollectionUtils.isNotEmpty(tempList)) {
                    //保存到redis
                    try {
                        tempList.stream().forEach(x -> {
                            stringRedisTemplate.opsForValue().set(x, "1");
                        });
                    } catch (Exception e) {
                        log.error("Job生成二维码保存到redis异常", e.getMessage());
                    }
                    //保存到数据库
                    try {
                        List<JobParamRecord> list = Lists.newArrayListWithCapacity(2000);
                        tempList.stream().forEach(x -> {
                            JobParamRecord jobParamRecord = new JobParamRecord();
                            jobParamRecord.setId(UUIDUtils.getUUID()).setDirectory(x)
                            .setFlag(1);
                            list.add(jobParamRecord);
                        });
                        jobParamRecordService.saveBatch(list);
                    } catch (Exception e) {
                        log.error("Job生成二维码保存到数据库异常", e.getMessage());
                    }
                }
                log.info("任务完成,生成二二维码数量：[{}]", tempList.size());
                stringRedisTemplate.opsForValue().set(redisKey, "1", 1L, TimeUnit.MINUTES);
            } catch (IOException e) {
                log.info(e.getMessage());
                e.printStackTrace();
            } finally {
                executor.shutdown();
                tempList.clear();
            }
        } else {
            log.info("请稍后再试....");
        }

    }


    /**
     * 等待线程执行完毕
     *
     * @param imageSize         次数
     * @param completionService 线程池
     * @return List
     */
    private List<String> waitGenerateQrcode(int imageSize, CompletionService<List> completionService) {
        List list = Lists.newArrayListWithCapacity(1);
        for (int i = 0; i < imageSize; i++) {
            try {
                list.addAll(completionService.take().get());
            } catch (Exception e) {
                log.info("生成二维码到指定目录:[{}]", e.getCause());
            }
        }
        return list;
    }

    /**
     * 生成二维码到指定目录
     *
     * @param imageSize         次数
     * @param completionService 线程池
     * @param wxaQrcodeApi1     wxApi
     * @param dey               日期目录
     * @param atomicLong        引用
     */
    private void generateQrcode(int imageSize, CompletionService<List> completionService, WxaQrcodeApi wxaQrcodeApi1,
                                String dey, AtomicLong atomicLong) {
        log.info("生成二维码到指定目录");
        for (int i = 0; i < imageSize; i++) {
            try {
                //生成二维码到指定目录
                completionService.submit(new ImagesTask(wxaQrcodeApi1, dey, atomicLong));
            } catch (Exception e) {
                log.info("生成二维码到指定目录:[{}]", e.getCause());
            }
        }
    }

}
