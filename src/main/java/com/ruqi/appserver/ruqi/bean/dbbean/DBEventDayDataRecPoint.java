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
}
