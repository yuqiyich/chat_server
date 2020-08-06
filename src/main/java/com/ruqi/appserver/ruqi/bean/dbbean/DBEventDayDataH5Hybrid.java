package com.ruqi.appserver.ruqi.bean.dbbean;

/**
 * sql查询返回的每一天，每个平台，每个用户的失败记录次数列表item
 */
public class DBEventDayDataH5Hybrid {
    // 日期
    public String date;
    // 平台
    public String platform;
    public long userId;
    public long failCount;
}
