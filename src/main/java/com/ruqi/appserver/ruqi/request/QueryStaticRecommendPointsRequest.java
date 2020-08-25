package com.ruqi.appserver.ruqi.request;

/**
 * Author:liangbingkun
 * Time:2020/8/24
 * Description:
 */
public class QueryStaticRecommendPointsRequest extends BaseRequest {
    private String env;
    private String cityCode;

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getEnv() {
        return env;
    }
}
