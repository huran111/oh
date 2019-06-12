package com.tykj.listener;


import com.tykj.wx.service.ITmpQrcodeService;
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
    private ITmpQrcodeService tmpQrcodeService;
    private String id;

    public AddImageTask(String id, ITmpQrcodeService tmpQrcodeService) {
        this.id = id;
        this.tmpQrcodeService = tmpQrcodeService;
    }

    @Override
    public void run() {
        log.info("添加任务到队列:[{}]", id);
        SysDeleteData.addImageData(id,tmpQrcodeService);
    }
}
