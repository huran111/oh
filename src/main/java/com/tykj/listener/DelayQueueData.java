package com.tykj.listener;/**
 * <pre>
 * TODO：
 * </pre>
 *
 * @author BUCHU
 * @date 2019/6/10
 */

import java.util.concurrent.DelayQueue;

/**
 * @author 胡冉
 * @ClassName DelayQueueData
 * @Date 2019/6/10 9:51
 * @Version 2.0
 */
public class DelayQueueData {
    // 延时队列 ,消费者从其中获取消息进行消费
    protected static DelayQueue<DelayQueueDeleteImages> queue = new DelayQueue<DelayQueueDeleteImages>();


    public static void addImageData(String id) {
        queue.offer(new DelayQueueDeleteImages(id, 1000*60*5));
    }
    
}
