package com.tykj.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.utils.DateUtils;
import com.tykj.wx.entity.InvalidQrparam;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.entity.TmpqrcodeRecord;
import com.tykj.wx.service.IInvalidQrparamService;
import com.tykj.wx.service.ITmpqrcodeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private IInvalidQrparamService iInvalidQrparamService;
    @Autowired
    private ITmpqrcodeRecordService tmpqrcodeRecordService;

    /**
     * 删除临时体验码
     */
    @Scheduled(fixedRate = 10000)
    private void configureTasks() {
        log.info("删除临时挪车体验码:[{}]", LocalDateTime.now());
        SysDeleteData.queue.keySet().forEach(key -> {
            String qrparam = key.split("-")[0];
            String timeap = key.split("-")[1];
            if (DateUtils.CreateDate() > Long.valueOf(timeap)) {
                log.info("删除:[{}]", key);
                FileUtils.deleteQuietly(new File("/home/images/tmpQrParam/" + key + ".png"));
                if (SysDeleteData.queue.containsKey(key)) {
                    InvalidQrparam invalidQrparam = new InvalidQrparam();
                    invalidQrparam.setId(qrparam);
                    iInvalidQrparamService.save(invalidQrparam);
                    boolean f = SysDeleteData.queue.get(key).remove(new QueryWrapper<TmpQrcode>().lambda().eq
                            (TmpQrcode::getQrParam, qrparam));
                    if (f) {
                        SysDeleteData.tmpRecord.add(qrparam);
                        SysDeleteData.queue.remove(key);
                    }
                }
            }
        });
    }

    /**
     * 删除临时挪车记录
     */
    @Scheduled(cron = "0 0 23 * * ?")
    private void deleteTmpQrParam() {
        log.info("删除临时挪车记录:[{}]", LocalDateTime.now());
        try {
            SysDeleteData.tmpRecord.forEach(x -> {
                boolean f = tmpqrcodeRecordService.remove(new QueryWrapper<TmpqrcodeRecord>()
                        .lambda().eq(TmpqrcodeRecord::getQrParam, x));
                if (f) {
                    SysDeleteData.tmpRecord.remove(x);
                }
            });
        } catch (Exception e) {
            log.info("删除临时挪车记录:[{}]", e.getCause());
        }
    }
}
