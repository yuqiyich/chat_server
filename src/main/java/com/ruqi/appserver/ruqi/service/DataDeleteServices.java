package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.kafka.BaseKafkaLogInfo;
import com.ruqi.appserver.ruqi.kafka.KafkaProducer;
import com.ruqi.appserver.ruqi.utils.DotEventDataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class DataDeleteServices {
    Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CRON_STATIC = "0 0 1 * * ?";

    @Autowired
    protected KafkaProducer kafkaProducer;

    @Autowired
    DotEventInfoWrapper dotEventInfoWrapper;

    @Scheduled(cron = CRON_STATIC)
    public void deleteEventData() throws InterruptedException {
        // 每天凌晨1点删除7天前的H5埋点数据
        long delSize = dotEventInfoWrapper.deleteEventData(DotEventDataUtils.getInstance().getSqlStr("h5hybrid",
                DotEventDataUtils.getInstance().getEventTypeKeys(false)), 7);
        String delH5DataTips = "清理H5加载埋点数据，size=" + delSize;
        logger.info(delH5DataTips);
        kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.INFO, delH5DataTips);
    }

}
