package com.ruqi.appserver.ruqi.bean.response;

/**
 * H5内嵌加载，每日每个key的总量数据
 */
public class EventDayDataH5Hybrid {
    // 日期
    public String date;
    //---------Android---------
    // 加载成功数目
    public long successCountA;
    // 加载失败数目
    public long failCountA;
    // 重试加载成功数目
    public long reloadSuccessCountA;
    // 重试加载失败数目
    public long reloadFailCountA;
    //---------iOS---------
    // 加载成功数目
    public long successCountI;
    // 加载失败数目
    public long failCountI;
    // 重试加载成功数目
    public long reloadSuccessCountI;
    // 重试加载失败数目
    public long reloadFailCountI;

    // 两个失败总数当日最大的用户id、数量、平台
    public long moreFailUserId;
    public long moreFailCount;
    public String moreFailPlatform;

    public long failUserCountTotal;
    //---------Android---------
    // 失败总人数
    public long failUserCountAndroid;
    //---------iOS---------
    // 失败总人数
    public long failUserCountIOS;
}
