package com.tykj.job;

import com.jfinal.weixin.sdk.utils.IOUtils;
import com.jfinal.wxaapp.api.WxaQrcodeApi;
import com.tykj.utils.UUIDUtils;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class ImagesTask implements Callable<Long> {
    private WxaQrcodeApi wxaQrcodeApi;
    private String day;
    private AtomicLong atomicLong;

    public ImagesTask(WxaQrcodeApi wxaQrcodeApi1, String day, AtomicLong atomicLong) {
        this.wxaQrcodeApi = wxaQrcodeApi1;
        this.day = day;
        this.atomicLong=atomicLong;
    }

    @Override
    public Long call() throws Exception {
        String qrParamId = UUIDUtils.getUUID();
        InputStream inputStream = wxaQrcodeApi.getUnLimit(qrParamId, "pages/home/home");
        IOUtils.toFile(inputStream, new File("/home/images/qrParam/" + day + "/" + qrParamId + ".png"));
        return atomicLong.incrementAndGet();
    }
}
