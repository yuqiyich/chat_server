package com.ruqi.appserver.ruqi.config;

import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import com.ruqi.appserver.ruqi.controller.PointController;
import com.ruqi.appserver.ruqi.kafka.BaseKafkaLogInfo;
import com.ruqi.appserver.ruqi.kafka.KafkaProducer;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池配置
 * @author yich
 *
 */
@Configuration
@EnableAsync
public class ThreadPoolTaskConfig {
    private static Logger logger = LoggerFactory.getLogger("ThreadPoolTaskConfig");
    private static int mDiscardCount = 0;
/**
 *   默认情况下，在创建了线程池后，线程池中的线程数为0，当有任务来之后，就会创建一个线程去执行任务，
 *	当线程池中的线程数目达到corePoolSize后，就会把到达的任务放到缓存队列当中；
 *  当队列满了，就继续创建线程，当线程数量大于等于maxPoolSize后，开始使用拒绝策略拒绝
 */

    /** 核心线程数（默认线程数） */
    private static final int corePoolSize =  Runtime.getRuntime().availableProcessors();//IO密集型
    /** 最大线程数 */
    private static final int maxPoolSize = corePoolSize+1;//使用与核心线程多几个
    /** 允许线程空闲时间（单位：默认为秒） */
    private static final int keepAliveTime = 60;
    /** 缓冲队列大小 */
    public static final int queueCapacity = 1000;//上一次是200感觉比较少，调整到1000
    /** 线程池名前缀 */
    private static final String threadNamePrefix = "Async-Service-";


    @Bean("taskExecutor") // bean的名称，默认为首字母小写的方法名
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveTime);
        executor.setThreadNamePrefix(threadNamePrefix);

        // 线程池对拒绝任务的处理策略
        // CallerRunsPolicy：实在是处理不了就丢弃掉
        executor.setRejectedExecutionHandler(new WrapperDiscardPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }

    static class WrapperDiscardPolicy  extends ThreadPoolExecutor.DiscardPolicy{
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            super.rejectedExecution(r, e);
            mDiscardCount++;
            String log="has discard this task,and all discard count:"+mDiscardCount;
            logger.error(log);
        }
    }

}