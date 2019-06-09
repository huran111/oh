package com.tykj;

import com.tykj.common.DelayQueueDeleteImages;

import java.util.concurrent.DelayQueue;

/**
 * @Description TODO
 * @auther huran
 * @date
 **/
public class QueueTest {
    // 延时队列 ,消费者从其中获取消息进行消费
    private  static DelayQueue<DelayQueueDeleteImages> queue;
    public static void main(String[] args) throws InterruptedException {
        // 创建延时队列
        DelayQueue<DelayQueueDeleteImages> queue = new DelayQueue<DelayQueueDeleteImages>();
        DelayQueueDeleteImages m1=new DelayQueueDeleteImages("1",3000);
        queue.offer(m1);
        queue.offer(m1);
        while (queue.size()>0){
            System.out.println(queue.take().toString());
        }

    }
}
