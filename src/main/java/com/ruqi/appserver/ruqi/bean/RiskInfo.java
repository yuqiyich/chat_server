package com.ruqi.appserver.ruqi.bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 *对应的风险表
 *
 * @author yich
 */
public class RiskInfo {
    //base info
    public String deviceId;
    public long userId;
    public String deviceBrand;
    public String systemVersion;
    public String netState;
    public String appVersion;
    public String locationName;
    public String appId;
    public Date createTime;
    public String channel;
    public BigDecimal locationLat;
    public BigDecimal locationLng;
    //riskinfo
    public String riskType;
    public String riskDetail;

}
