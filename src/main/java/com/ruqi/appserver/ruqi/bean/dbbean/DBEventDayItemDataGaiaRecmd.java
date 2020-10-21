package com.ruqi.appserver.ruqi.bean.dbbean;

/**
 * sql查询返回的每一天，每个平台，盖亚兜底推荐点记录总次数列表item
 */
public class DBEventDayItemDataGaiaRecmd {
    // 日期
    public String date;
    // 平台
    public String platform;
//    public String eventKey;
    public long totalCount;
}
