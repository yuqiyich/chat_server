package com.ruqi.appserver.ruqi.schedule;

import com.ruqi.appserver.ruqi.service.IUserService;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling // 2.开启定时任务
public class TokenSchedule {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected RedisUtil redisUtil;

    @Autowired
    private IUserService userService;

    //3.添加定时任务
    @Scheduled(cron = "0 0 0 1 * ?") // 每个月的1号的0点进行token更新
//    @Scheduled(cron = "0/30 * * * * ?") // 测试，30s更新一次
    private void configureTasks() {
        logger.info("定时更新token");

        userService.updateAllUserToken();

        redisUtil.deleteKeys(RedisUtil.GROUP_USER_INFO);
    }
}
