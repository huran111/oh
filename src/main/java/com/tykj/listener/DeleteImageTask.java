package com.tykj.listener;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tykj.wx.entity.TmpQrcode;
import com.tykj.wx.service.ITmpQrcodeService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * @author 胡冉
 * @ClassName DeleteImageTask
 * @Description: 删除图片
 * @Date 2019/6/10 9:43
 * @Version 2.0
 */
@Slf4j
public class DeleteImageTask extends Thread {
    private boolean running = true;


    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                if (DelayQueueData.queue.size() > 0) {
                    String imageName = DelayQueueData.queue.take().getId();
                    ITmpQrcodeService tmpQrcodeService = DelayQueueData.queue.take().getTmpQrcodeService();
                    if (null != tmpQrcodeService) {
                        log.info("删除数据库:[{}]",imageName);
                        tmpQrcodeService.remove(new QueryWrapper<TmpQrcode>().lambda().eq(TmpQrcode::getQrParam,
                                imageName));
                    }
                    if (StringUtils.isNotEmpty(imageName)) {
                        log.info("删除临时二维码:[{}]", imageName);
                        FileUtils.deleteQuietly(new File("/home/images/tmpQrParam/" + imageName + ".png"));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
