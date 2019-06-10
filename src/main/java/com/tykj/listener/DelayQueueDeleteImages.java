package com.tykj.listener;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @auther huran
 * @date
 **/
@Data

public class DelayQueueDeleteImages implements Delayed {
    private String id;
    private long time;
    public DelayQueueDeleteImages(String id, long delayTime){
        this.id=id;
        this.time= TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();  ;
    }
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.time - System.nanoTime(), TimeUnit.NANOSECONDS);
    }
    @Override
    public int compareTo(Delayed delayed) {
        DelayQueueDeleteImages msg = (DelayQueueDeleteImages) delayed;
        return Integer.valueOf(this.id) > Integer.valueOf(msg.id) ? 1
                : (Integer.valueOf(this.id) < Integer.valueOf(msg.id) ? -1 : 0);
    }
}
