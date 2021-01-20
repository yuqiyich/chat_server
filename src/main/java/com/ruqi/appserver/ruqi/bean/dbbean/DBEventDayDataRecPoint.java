package com.ruqi.appserver.ruqi.bean.dbbean;

import java.util.Date;

/**
 * @author ZhangYu
 * @date 2020/11/27
 * @desc sql查询返回的每一天，每个平台，每个用户的失败记录次数列表item
 */
public class DBEventDayDataRecPoint {
    // 日期
    public Date date;
    public String env;

    public int didi_total_count_android;
    public int didi_total_count_ios;
    public int didi_order_count_android;
    public int didi_order_count_ios;

    public int tencent_total_count_android;
    public int tencent_total_count_ios;
    public int tencent_order_count_android;
    public int tencent_order_count_ios;

    public int gaia_total_count_android;
    public int gaia_total_count_ios;
    public int gaia_order_count_android;
    public int gaia_order_count_ios;

    public int tx_geo_total_count_android; // '腾讯GEO总数-Android'
    public int tx_geo_order_count_android; // '腾讯GEO下单数-Android'
    public int tx_geo_total_count_ios; // '腾讯GEO总数-iOS'
    public int tx_geo_order_count_ios; // '腾讯GEO下单数-iOS'

    public int app_total_count_android; // 'APP兜底总数-Android'
    public int app_order_count_android; // 'APP兜底下单数-Android'
    public int app_total_count_ios; // 'APP兜底总数-iOS'
    public int app_order_count_ios; // 'APP兜底下单数-iOS'

    public int app_geo_total_count_android; // 'APPGEO总数-Android'
    public int app_geo_order_count_android; // 'APPGEO下单数-Android'
    public int app_geo_total_count_ios; // 'APPGEO总数-iOS'
    public int app_geo_order_count_ios; // 'APPGEO下单数-iOS'

    public int fail_count_android; // '兜底失败-Android'
    public int fail_count_ios; // '兜底失败-iOS'
}
