package com.tykj.listener;/**
 * <pre>
 * TODO：
 * </pre>
 *
 * @author BUCHU
 * @date 2019/6/10
 */

import com.tykj.utils.DateUtils;
import com.tykj.wx.service.ITmpQrcodeService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 胡冉
 * @ClassName DelayQueueData
 * @Date 2019/6/10 9:51
 * @Version 2.0
 */
public class MapImageData {
    // 延时队列 ,消费者从其中获取消息进行消费
    protected static Map<String, ITmpQrcodeService> queue = new ConcurrentHashMap<>();

    public static void addImageData(String id, ITmpQrcodeService tmpQrcodeService) {
        String key = String.format("%s-%s", id, String.valueOf(DateUtils.expriredDate(DateUtils.CreateDate())));
        queue.put(key, tmpQrcodeService);
    }
}
