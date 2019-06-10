package com.tykj.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author 胡冉
 * @ClassName ImageListener
 * @Date 2019/6/10 9:41
 * @Version 2.0
 */
@Component
public class ImageListener {

    private DeleteImageTask deleteImageTask = new DeleteImageTask();

    @PostConstruct
    public void init() {
        System.out.println("初始化启动.....");
        deleteImageTask.start();


    }

    @PreDestroy
    public void destory() {
        deleteImageTask.setRunning(false);
    }

}
