package com.tykj.job;

import com.tykj.wx.service.IJobParamRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author 胡冉
 * @ClassName MoveQrParamThread
 * @Description: 移动二维码到指定目录线程
 * @Date 2019/7/26 19:23
 * @Version 2.0
 */
@Slf4j
public class MoveQrParamThread extends Thread {
    private StringRedisTemplate stringRedisTemplate;
    private IJobParamRecordService jobParamRecordService;
    private String qrParam;

    public MoveQrParamThread(String qrParam, StringRedisTemplate stringRedisTemplate, IJobParamRecordService
            jobParamRecordService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jobParamRecordService = jobParamRecordService;
        this.qrParam = qrParam;
    }

    @Override
    public void run() {

    }
}
