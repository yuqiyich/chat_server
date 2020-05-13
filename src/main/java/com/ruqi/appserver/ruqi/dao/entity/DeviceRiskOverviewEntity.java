package com.ruqi.appserver.ruqi.dao.entity;


import java.util.Date;

/**
 * 查询报警总览列表
 * <p>
 * 风险 类型统计：ID（行号）、风险类型、记录开始时间、记录结束时间、应用名称、记录总数量
 * APP版本号 类型统计：ID（行号）、APP版本号、记录开始时间、记录结束时间、应用名称、记录总数量
 * 设备型号 类型统计：ID（行号）、设备型号、记录开始时间、记录结束时间、应用名称、记录总数量
 * 设备品牌 类型统计：ID（行号）、设备品牌、记录开始时间、记录结束时间、应用名称、记录总数量
 * 用户手机号 类型统计：ID（行号）、用户手机号、记录开始时间、记录结束时间、应用名称、记录总数量
 * 设备ID 类型统计：ID（行号）、设备ID、记录开始时间、记录结束时间、应用名称、记录总数量
 * 安卓系统版本 类型统计：ID（行号）、安卓系统版本、记录开始时间、记录结束时间、应用名称、记录总数量
 */
public class DeviceRiskOverviewEntity {
    public String riskType;
    public long totalSize;
    public String appName;
    public Date minTime;
    public Date maxTime;
    public String appVersionCode;
    //    public String appVersionName;
    public String deviceModel;
    public String deviceBrand;
    public String userPhone;

    public String deviceId;


    public String systemVersion;


//    public String channel;


    //status info
//    public Date createTime;
//    public float locationLat;
//    public float locationLng;
//    public String locationName;
//    public String netState;
//    public String requestIp;
    //risk info
//    public String scene;//使用的场景
//    public String ext;//使用额外字段标识
//    public String riskDetail;
}
