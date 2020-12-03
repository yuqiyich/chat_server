package com.ruqi.appserver.ruqi.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author ZhangYu
 * @date 2020/11/30
 * @desc 返回数据 日统计推荐上车点事件数据
 */
@ApiModel
public class RecPointDayData {
    // 日期
    public String date;
    public String env;
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
}
