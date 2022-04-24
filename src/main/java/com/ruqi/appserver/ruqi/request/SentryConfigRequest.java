package com.ruqi.appserver.ruqi.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * Author:liangbingkun
 * Time:2020/8/24
 * Description:
 */
@ApiModel(value = "SentryConfigRequest")
public class SentryConfigRequest extends BaseRequest {
    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "mobile不能为空")
    private String mobile;
    @ApiModelProperty(value = "设备id")
    @NotBlank(message = "deviceId不能为空")
    private String deviceId;
    @ApiModelProperty(value = "平台值" ,allowableValues = "ios,android")
    @NotBlank(message = "platform不能为空")
    private String platform;
    @NotBlank(message = "environment不能为空")
    @ApiModelProperty(value = "请求环境：根据终端所处环境 ，例如android的乘客端有：dev，test，pro" )
    private String environment;
    @ApiModelProperty(value = "城市编码" )
    @NotBlank(message = "cityCode不能为空")
    private String cityCode;
    @ApiModelProperty(value = "区域编码" )
    @NotBlank(message = "areaCode不能为空")
    private String areaCode;
    @NotBlank(message = "latitude不能为空")
    private String latitude;
    @NotBlank(message = "longitude不能为空")
    private String longitude;

    @ApiModelProperty(value = "当前项目名，v1版本没有该字段，v2使用枚举值枚举值", allowableValues = "ruqi_client,ruqi_driver" )
    private String project;

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

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
