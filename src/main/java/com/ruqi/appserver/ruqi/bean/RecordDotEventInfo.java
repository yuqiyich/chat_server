package com.ruqi.appserver.ruqi.bean;

import java.util.Date;

/**
 * 为了适配layui table 控件的数据控制
 */
public class RecordDotEventInfo {

    public long id;
    public String appName;
    public String platform;
    public long userId;
    public String nickName;
    public int riskLevel;
    //    public String userPhone;
    public String eventKey;
    public String eventKeyName;
    //device info
    public String deviceId;
    public String deviceBrand;
    public String deviceModel;
    public String systemVersion;
    public String appVersionName;
    public int appVersionCode;
    public String channel;
    //status info
    public Date createTime;
    public Date recordTime;
    public float locationLat;
    public float locationLng;
    public String locationName;
    public String netState;
    public String requestIp;
    public String ext;//使用额外字段标识
    public String eventDetail;
    public String scene;
    public String orderId;
    public int userType;

    public double startLng;
    public double startLat;
}
