package com.ruqi.appserver.ruqi.bean.response;

/**
 * 推荐上车点盖亚兜底7天内数据总量
 */
public class EventDataGaiaRecmd {
    // 日期
    public String date;
    //---------Android---------
    // 截止日期，盖亚兜底的总数量
    public long gaiaRecmdCountA;
    // 截止日期，盖亚兜底且带订单的总数量
    public long gaiaRecmdOrderCountA;
    //---------iOS---------
    // 加载成功数目
    public long gaiaRecmdCountI;
    // 加载失败数目
    public long gaiaRecmdOrderCountI;
}
