package com.ruqi.appserver.ruqi.bean;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseRecordInfo {
    public static final String PLATFORM_ANDROID = "Android";
    public static final String PLATFORM_IOS = "iOS";

    //device info
    public String deviceId;
    public String deviceBrand;
    public String deviceModel;
    public String systemVersion;
    public String appVersionName;
    public int appVersionCode;
    public int appId;
    public String channel;
    public String platform;
    //status info
    // 事件发生时间，接口上报
    public long createTime;
    // 事件记录时间，保存在数据库时的服务器当前时间
    public Date recordTime;
    public long userId;
    public float locationLat;
    public float locationLng;
    public String locationName;
    public String netState;
    public String requestIp;

    /*****
     *查询字段参数
     */
    public String duringTime;
    public Date startDate;
    public Date endDate;

    public String getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(String duringTime) {
        this.duringTime = duringTime;
        createStartEndDate();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void createStartEndDate() {
        if (!StringUtils.isEmpty(duringTime)) {
            String[] str = duringTime.split("~");
            if (str.length == 2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    startDate = format.parse(str[0].trim());
                    endDate = format.parse(str[1].trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*****
     *查询字段参数
     */

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getNetState() {
        return netState;
    }

    public void setNetState(String netState) {
        this.netState = netState;
    }


    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public float getLocationLat() {
        return locationLat;
    }

    public void setLocationLat(float locationLat) {
        this.locationLat = locationLat;
    }

    public float getLocationLng() {
        return locationLng;
    }

    public void setLocationLng(float locationLng) {
        this.locationLng = locationLng;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }
}
