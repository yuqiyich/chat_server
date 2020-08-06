package com.ruqi.appserver.ruqi.kafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaDemoApplicationTests {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaProducer kafkaProducer;

    private Clock clock = Clock.systemDefaultZone();
    private long begin;
    private long end;

    @Before
    public void init() {
        begin = clock.millis();
    }

    @Test
    public void send() {
        kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.WARN, "url:http://ruqi6.com;request:[req];response:[res]");
    }

    @After
    public void end() {
        end = clock.millis();
        logger.info("Spend {} millis .", end - begin);
    }

}
