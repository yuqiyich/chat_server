package com.ruqi.appserver.ruqi.bean;

import java.util.Date;

public class BaseUploadRecordInfo {

    //device info
    public String deviceId;
    public String deviceBrand;
    public String deviceModel;
    public String systemVersion;
    public String appVersionName;
    public int appVersionCode;
    public String channel;
    public String platform;
    //status info
    // 事件发生时间，接口上报
    public long createTime;
    public float locationLat;
    public float locationLng;
    public String locationName;
    public String netState;
    public String ext;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
