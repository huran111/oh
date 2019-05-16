package com.tykj.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.service.ITmpQrcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Description 定时删除体验码
 * @auther huran
 * @date
 **/
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling
public class SaticScheduleTask {
    @Autowired
    private ITmpQrcodeService tmpQrcodeService;

    //或直接指定时间间隔，例如：5秒
    @Scheduled(cron = "0 0/5 * * * ?")
    private void configureTasks() {
        QueryWrapper<TmpQrcode> qrcodeQueryWrapper = new QueryWrapper<>();
        qrcodeQueryWrapper.lambda().ge(TmpQrcode::getCreateTime, new Date());
        List<TmpQrcode> tmpQrcodeList = tmpQrcodeService.list(qrcodeQueryWrapper);
        Optional.ofNullable(tmpQrcodeList).ifPresent(x -> {
            tmpQrcodeService.removeByIds(tmpQrcodeList);
        });
        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
    }
}
