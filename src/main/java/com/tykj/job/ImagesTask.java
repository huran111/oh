package com.tykj.job;

import com.google.common.collect.Lists;
import com.jfinal.weixin.sdk.utils.IOUtils;
import com.jfinal.wxaapp.api.WxaQrcodeApi;
import com.tykj.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.context.annotation.Scope;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ImagesTask implements Callable<List> {
    private WxaQrcodeApi wxaQrcodeApi;
    private String day;
    private AtomicLong atomicLong;

    public ImagesTask(WxaQrcodeApi wxaQrcodeApi1, String day, AtomicLong atomicLong) {
        this.wxaQrcodeApi = wxaQrcodeApi1;
        this.day = day;
        this.atomicLong = atomicLong;
    }


    @Override
    public List<String> call() throws Exception {
        List list = Lists.newArrayList();
        String qrParamId = UUIDUtils.getUUID();
        InputStream inputStream = wxaQrcodeApi.getUnLimit(qrParamId, "pages/home/home");
        //压缩图片
        Thumbnails.of(inputStream).scale(0.8).outputQuality(1f).toFile(new File("/home/images/qrParam/" + day + "/" +
                qrParamId + ".png"));
        String key = String.format("%s.png-%s", qrParamId, day);
        list.add(key);
        //  IOUtils.toFile(inputStream, new File("/home/images/qrParam/" + day + "/" + qrParamId + ".png"));
        return list;
    }
}
