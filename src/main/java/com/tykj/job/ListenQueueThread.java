package com.tykj.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.wx.entity.JobParamRecord;
import com.tykj.wx.service.IJobParamRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 胡冉
 * @ClassName MoveQrParamThread
 * @Description: 监听队列
 * @Date 2019/7/26 19:23
 * @Version 2.0
 */
@Slf4j
public class ListenQueueThread extends Thread {
    private LinkedBlockingQueue queue;
    private IJobParamRecordService jobParamRecordService;

    public ListenQueueThread(LinkedBlockingQueue queue, IJobParamRecordService jobParamRecordService) {
        this.queue = queue;
        this.jobParamRecordService = jobParamRecordService;
    }

    @Override
    public void run() {
        log.info("监听队列开始...........");
        while (true) {
            try {
                String d = (String) this.queue.take();
                log.info("线程[{}]更新数据库[{}]", Thread.currentThread().getName(), d);
                List<JobParamRecord> jobParamRecordList = this.jobParamRecordService.list(new QueryWrapper<JobParamRecord>()
                        .lambda().eq(JobParamRecord::getDirectory, d));
                if (CollectionUtils.isNotEmpty(jobParamRecordList)) {
                    jobParamRecordList.stream().forEach(x -> {
                        x.setFlag(2);
                        this.jobParamRecordService.saveOrUpdate(x);
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
