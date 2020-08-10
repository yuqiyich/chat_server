//package com.ruqi.appserver.ruqi.kafka;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class KafkaConsumer {
//
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @KafkaListener(topics = KafkaTopic.TOPIC_GAIA_SERVICE, groupId = "${spring.kafka.consumer.group-id}")
//    private void kafkaConsumer(ConsumerRecord<String, BaseKafkaLogInfo> consumerRecord) {
//        logger.info("kafkaConsumer: topic = {}, msg = {}", consumerRecord.topic(), consumerRecord.value());
//    }
//}
