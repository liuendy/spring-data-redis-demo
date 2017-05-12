package com.example;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * schedule task
 * Created by zhile on 2017/5/11 0011.
 */
@Component
public class SchedulerTask {

    private int count = 0;

    @Scheduled(cron = "0/30 * * * * ?")
    public void doTask() {
        System.out.println("schedule task running : " + count++);
    }
}
