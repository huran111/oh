package com.tykj.listener;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * @author 胡冉
 * @ClassName DeleteImageTask
 * @Description: 删除图片
 * @Date 2019/6/10 9:43
 * @Version 2.0
 */
@Slf4j
public class AddImageTask extends Thread {
    private String id;
    public AddImageTask(String id){
            this.id=id;
    }
    @Override
    public void run() {
        log.info("添加任务到队列:[{}]",id);
        DelayQueueData.addImageData(id);
    }
}
