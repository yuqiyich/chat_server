package com.ruqi.appserver.ruqi.bean;

/**
 * Author:liangbingkun
 * Time:2020/8/21
 * Description: sentry 行政区域配置
 */
public class SentryAreaEntity {
    private String areacode;
    private String citycode;

    public String getAreacode() {
        return areacode;
    }

    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
