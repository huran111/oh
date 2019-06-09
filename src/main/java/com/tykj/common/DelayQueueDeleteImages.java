package com.tykj.common;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @auther huran
 * @date
 **/
@Data
public class DelayQueueDeleteImages implements Delayed {
    private String id;
    private long time;
    public DelayQueueDeleteImages(String id,long delayTime){
        this.id=id;
        this.time= TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();  ;
    }
    //// 延迟任务是否到时就是按照这个方法判断如果返回的是负数则说明到期否则还没到期
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.time - System.nanoTime(), TimeUnit.NANOSECONDS);
    }
    //// 自定义实现比较方法返回 1 0 -1三个参数
    @Override
    public int compareTo(Delayed delayed) {
        DelayQueueDeleteImages msg = (DelayQueueDeleteImages) delayed;
        return Integer.valueOf(this.id) > Integer.valueOf(msg.id) ? 1
                : (Integer.valueOf(this.id) < Integer.valueOf(msg.id) ? -1 : 0);
    }
}
