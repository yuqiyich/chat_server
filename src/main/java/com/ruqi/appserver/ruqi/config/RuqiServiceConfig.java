package com.ruqi.appserver.ruqi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Author:liangbingkun
 * Time:2020/7/30
 * Description:
 */

@ConfigurationProperties(prefix = "service")
@Component
public class RuqiServiceConfig {
    private String privateKey;

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
