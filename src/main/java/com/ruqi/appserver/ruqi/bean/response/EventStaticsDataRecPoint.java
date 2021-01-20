package com.ruqi.appserver.ruqi.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author ZhangYu
 * @date 2021/1/20
 * @desc 统计乘客推荐上车点数据
 */
@ApiModel
public class EventStaticsDataRecPoint {
    public String env;
    public String firstDate;
    public String endDate;

    @ApiModelProperty(value = "滴滴吸附总数-Android")
    public int didiTotalCountAndroid;
    @ApiModelProperty(value = "滴滴吸附总数-IOS")
    public int didiTotalCountIOS;
    @ApiModelProperty(value = "滴滴吸附下单数-Android")
    public int didiOrderCountAndroid;
    @ApiModelProperty(value = "滴滴吸附下单数-IOS")
    public int didiOrderCountIOS;

    @ApiModelProperty(value = "腾讯吸附总数-Android")
    public int tencentTotalCountAndroid;
    @ApiModelProperty(value = "腾讯吸附总数-IOS")
    public int tencentTotalCountIOS;
    @ApiModelProperty(value = "腾讯吸附下单数-Android")
    public int tencentOrderCountAndroid;
    @ApiModelProperty(value = "腾讯吸附下单数-IOS")
    public int tencentOrderCountIOS;

    @ApiModelProperty(value = "盖亚吸附总数-Android")
    public int gaiaTotalCountAndroid;
    @ApiModelProperty(value = "盖亚吸附总数-IOS")
    public int gaiaTotalCountIOS;
    @ApiModelProperty(value = "盖亚吸附下单数-Android")
    public int gaiaOrderCountAndroid;
    @ApiModelProperty(value = "盖亚吸附下单数-IOS")
    public int gaiaOrderCountIOS;

    @ApiModelProperty(value = "腾讯GEO总数-Android")
    public int txGeoTotalCountAndroid;
    @ApiModelProperty(value = "腾讯GEO下单数-Android")
    public int txGeoOrderCountAndroid;
    @ApiModelProperty(value = "腾讯GEO总数-iOS")
    public int txGeoTotalCountIOS;
    @ApiModelProperty(value = "腾讯GEO下单数-iOS")
    public int txGeoOrderCountIOS;

    @ApiModelProperty(value = "'APP兜底总数-Android")
    public int appTotalCountAndroid;
    @ApiModelProperty(value = "APP兜底下单数-Android")
    public int appOrderCountAndroid;
    @ApiModelProperty(value = "APP兜底总数-iOS")
    public int appTotalCountIOS;
    @ApiModelProperty(value = "APP兜底下单数-iOS")
    public int appOrderCountIOS;

    @ApiModelProperty(value = "APPGEO总数-Android")
    public int appGeoTotalCountAndroid;
    @ApiModelProperty(value = "APPGEO下单数-Android")
    public int appGeoOrderCountAndroid;
    @ApiModelProperty(value = "APPGEO总数-iOS")
    public int appGeoTotalCountIOS;
    @ApiModelProperty(value = "APPGEO下单数-iOS")
    public int appGeoOrderCountIOS;

    @ApiModelProperty(value = "兜底失败-Android")
    public int failCountAndroid;
    @ApiModelProperty(value = "兜底失败-iOS")
    public int failCountIOS;
}
