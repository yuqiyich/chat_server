package com.ruqi.appserver.ruqi.bean;

/**
 * Author:liangbingkun
 * Time:2020/8/21
 * Description: sentry 平台环境配置
 */
public class SentryPlatformEntity {
    private String platform;
    private String environment;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
