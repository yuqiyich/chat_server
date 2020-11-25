package com.ruqi.appserver.ruqi.bean;

import java.util.Map;

/**
 * @author ZhangYu
 * @date 2020/5/11
 * @desc 推荐上车点降级生效记录
 */
public class DotEventInfo extends BaseRecordInfo {
    public static final String NAME_USER_TYPE = "userType";

    /**
     * <p>
     * 事件埋点记录统计KEY。
     * EVENT_MULIT_ORDER 乘客端，多开，且订单创建成功，无需预付费。eventDetail中记录订单ID。
     */
    public String eventKey;
    public String eventDetail;
    public String ext;
    /**
     * 订单id
     */
    public String orderId;
    /**
     * 场景
     * 1：接驾
     * 2：送驾
     */
    public String scene;

    /**
     * 查询时筛选用，1：orderId有值的数据；2：orderId无值的数据；默认其他情况返回所有数据
     */
    public String orderIdFilter;
    /**
     * 事件类别，一个类别下有固定的几个eventKey，用于查询某类别下的全部eventKey的记录
     */
    public String eventType;

    /**
     * 以下额外数据在这里取值
     */
    public Map<String, Object> eventData;
    /**
     * 用户类型 可见用户类型. 1:APP通用 2:未登录用户 3:专车用户 4:顺风车乘客 5:顺风车车主
     */
    public int userType;

    @Override
    public String toString() {
        return "DotEventInfo{" +
                "eventKey='" + eventKey + '\'' +
                ", eventDetail='" + eventDetail + '\'' +
                ", ext='" + ext + '\'' +
                ", orderId='" + orderId + '\'' +
                ", scene='" + scene + '\'' +
                ", orderIdFilter='" + orderIdFilter + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventData=" + eventData +
                ", userType=" + userType +
                ", deviceId='" + deviceId + '\'' +
                ", deviceBrand='" + deviceBrand + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", systemVersion='" + systemVersion + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                ", appVersionCode=" + appVersionCode +
                ", appId=" + appId +
                ", channel='" + channel + '\'' +
                ", platform='" + platform + '\'' +
                ", createTime=" + createTime +
                ", recordTime=" + recordTime +
                ", userId=" + userId +
                ", locationLat=" + locationLat +
                ", locationLng=" + locationLng +
                ", locationName='" + locationName + '\'' +
                ", netState='" + netState + '\'' +
                ", requestIp='" + requestIp + '\'' +
                ", duringTime='" + duringTime + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
