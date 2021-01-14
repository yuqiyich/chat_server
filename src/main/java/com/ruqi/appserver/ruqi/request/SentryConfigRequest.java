package com.ruqi.appserver.ruqi.request;

import javax.validation.constraints.NotBlank;

/**
 * Author:liangbingkun
 * Time:2020/8/24
 * Description:
 */
public class SentryConfigRequest extends BaseRequest {
    @NotBlank(message = "mobile不能为空")
    private String mobile;
    @NotBlank(message = "deviceId不能为空")
    private String deviceId;
    @NotBlank(message = "platform不能为空")
    private String platform;
    @NotBlank(message = "environment不能为空")
    private String environment;
    @NotBlank(message = "cityCode不能为空")
    private String cityCode;
    @NotBlank(message = "areaCode不能为空")
    private String areaCode;
    @NotBlank(message = "latitude不能为空")
    private String latitude;
    @NotBlank(message = "longitude不能为空")
    private String longitude;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

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

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
