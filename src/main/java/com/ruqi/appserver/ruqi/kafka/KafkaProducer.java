package com.ruqi.appserver.ruqi.kafka;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Async
    public void sendLog(BaseKafkaLogInfo.LogLevel logLevel, String message) {
        BaseKafkaLogInfo baseKafkaLogInfo = new BaseKafkaLogInfo(logLevel, message);

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(KafkaTopic.TOPIC_GAIA_SERVICE, JSONObject.toJSONString(baseKafkaLogInfo));

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("kafka sendMessage error, throwable = {}, topic = {}, data = {}", throwable, KafkaTopic.TOPIC_GAIA_SERVICE, baseKafkaLogInfo);
            }

            @Override
            public void onSuccess(SendResult<String, String> stringDotaHeroSendResult) {
                logger.info("kafka sendMessage success topic = {}, data = {}", KafkaTopic.TOPIC_GAIA_SERVICE, baseKafkaLogInfo);
            }
        });
    }

}
