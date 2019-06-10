package com.tykj.listener;/**
 * <pre>
 * TODO：
 * </pre>
 *
 * @author BUCHU
 * @date 2019/6/10
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.wx.entity.TmpQrcode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.time.LocalDateTime;

/**
 * @author 胡冉
 * @ClassName SaticScheduleTask
 * @Date 2019/6/10 19:52
 * @Version 2.0
 */
@Slf4j
@Configuration
@EnableScheduling
public class SaticScheduleTask {
    @Scheduled(fixedRate = 10000)
    private void configureTasks() {
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
        MapImageData.queue.keySet().forEach(key -> {
            log.info("删除:[{}]", key);
            String timeap = key.split("-")[1];
            String qrparam = key.split("-")[0];
            if (System.currentTimeMillis() > Long.valueOf(timeap)) {
                FileUtils.deleteQuietly(new File("/home/images/tmpQrParam/" + key + ".png"));
                if (MapImageData.queue.containsKey(key)) {
                    MapImageData.queue.get(key).remove(new QueryWrapper<TmpQrcode>().lambda().eq
                            (TmpQrcode::getQrParam, qrparam));
                }

            }
        });
    }
}
