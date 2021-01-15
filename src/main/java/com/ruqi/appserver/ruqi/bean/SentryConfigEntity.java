package com.ruqi.appserver.ruqi.bean;

/**
 * Author:liangbingkun
 * Time:2020/8/21
 * Description: sentry 开关
 */
public class SentryConfigEntity {
    private String sentrySwitch;
    private String level;
    private String dns;

    public String getSentrySwitch() {
        return sentrySwitch;
    }

    public void setSentrySwitch(String sentrySwitch) {
        this.sentrySwitch = sentrySwitch;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getDns() {
        return dns;
    }
}
