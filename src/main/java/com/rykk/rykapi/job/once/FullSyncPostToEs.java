package com.rykk.rykapi.job.once;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

/**
 * 全量同步
 *
 * @author rykk
 * 
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {


    /**
     * 单次执行
     * @param args
     */
    @Override
    public void run(String... args) {

    }
}
