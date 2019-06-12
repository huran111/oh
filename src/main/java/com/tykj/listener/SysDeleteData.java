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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 胡冉
 * @ClassName DelayQueueData
 * @Date 2019/6/10 9:51
 * @Version 2.0
 */
public final class SysDeleteData {

    public static Map<String, ITmpQrcodeService> queue = new ConcurrentHashMap<>();
    public static List<String> tmpRecord = new CopyOnWriteArrayList<>();

    /**
     * 添加到队列进行删除
     *
     * @param id
     * @param tmpQrcodeService
     */
    public static void addImageData(String id, ITmpQrcodeService tmpQrcodeService) {
        String key = String.format("%s-%s", id, String.valueOf(DateUtils.expriredDate(DateUtils.CreateDate())));
        queue.put(key, tmpQrcodeService);
    }
}
