package com.ruqi.appserver.ruqi.bean.response;

/**
 * H5内嵌加载，每日每个key的总量数据
 */
public class EventDayDataH5Hybrid {
    // 日期
    public String date;
    // 加载成功数目
    public long successCount;
    // 加载失败数目
    public long failCount;
    // 重试加载成功数目
    public long reloadSuccessCount;
    // 重试加载失败数目
    public long reloadFailCount;
}
