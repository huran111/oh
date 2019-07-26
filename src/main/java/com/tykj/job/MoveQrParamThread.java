package com.tykj.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.wx.entity.JobParamRecord;
import com.tykj.wx.service.IJobParamRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 胡冉
 * @ClassName MoveQrParamThread
 * @Description: 移动二维码到指定目录-线程
 * @Date 2019/7/26 19:23
 * @Version 2.0
 */
@Slf4j
public class MoveQrParamThread extends Thread {
    private StringRedisTemplate stringRedisTemplate;
    private IJobParamRecordService jobParamRecordService;
    private String qrParam;
    private LinkedBlockingQueue queue;
    private static final String SOURCE_URL = "/home/images/qrParam";

    public MoveQrParamThread(String qrParam, StringRedisTemplate stringRedisTemplate, IJobParamRecordService
            jobParamRecordService, LinkedBlockingQueue queue) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jobParamRecordService = jobParamRecordService;
        this.qrParam = qrParam;
        this.queue = queue;
    }

    @Override
    public void run() {
        Set<String> stringSet = this.stringRedisTemplate.keys(String.format("%s*", qrParam));
        if (CollectionUtils.isNotEmpty(stringSet)) {
            for (String directory : stringSet) {
                String values = this.stringRedisTemplate.opsForValue().get(directory);
                if (StringUtils.isNotEmpty(values) && values.equals("1")) {
                    this.moveFileOrUpdateDb(this.stringRedisTemplate, directory, true);
                }
            }
        } else {
            List<JobParamRecord> jobParamRecordList = this.jobParamRecordService.list(new QueryWrapper<JobParamRecord>().lambda()
                    .like(JobParamRecord::getDirectory, this.qrParam).eq(JobParamRecord::getFlag, 1));
            if (CollectionUtils.isNotEmpty(jobParamRecordList)) {
                jobParamRecordList.forEach(x -> {
                    this.moveFileOrUpdateDb(this.stringRedisTemplate, x.getDirectory(), true);
                });
            }
        }
    }


    /**
     *
     *
     * @param stringRedisTemplate
     * @param directory
     */
    /**
     * 移动文件
     *
     * @param stringRedisTemplate reds工具类
     * @param directory           目录
     * @param var                 true添加队列 false不添加队列
     */
    private void moveFileOrUpdateDb(StringRedisTemplate stringRedisTemplate, String directory, boolean var) {
        String[] keys = directory.split("-");
        String png = keys[0];
        String day = keys[1];
        Path sourcePath = Paths.get(String.format("%s/%s/%s", this.SOURCE_URL, day, png));
        Path targatPath = Paths.get(this.SOURCE_URL + "/" + png);
        try {
            Files.copy(sourcePath, targatPath);
            this.stringRedisTemplate.opsForValue().set(directory, "2");
            //保存到队列
            if (var) {
                boolean flag = this.queue.offer(directory);
                if (flag) {
                    log.info("添加队列成功:[{}]", directory);
                } else {
                    log.info("队列已经满了:[{}]", directory);
                }
            }

        } catch (IOException e) {
            log.error("Copy File Exception :[{}],[{}]", sourcePath, e.getMessage());
        }
    }
}
