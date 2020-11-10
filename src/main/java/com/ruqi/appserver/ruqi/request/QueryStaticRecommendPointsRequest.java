package com.ruqi.appserver.ruqi.request;

import javax.validation.constraints.NotBlank;

/**
 * Author:liangbingkun
 * Time:2020/8/24
 * Description:
 */
public class QueryStaticRecommendPointsRequest extends BaseRequest {
    @NotBlank(message = "env不能为空")
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
