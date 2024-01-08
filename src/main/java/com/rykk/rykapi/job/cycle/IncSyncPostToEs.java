package com.rykk.rykapi.job.cycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 增量同步
 *
 * @author rykk
 * 
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class IncSyncPostToEs {


    /**
     * 每分钟执行一次run()函数
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {

    }
}
