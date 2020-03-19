package com.ruqi.appserver.ruqi.bean;

import com.aliyuncs.utils.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *对应的风险表
 *
 * @author yich
 */
public class RiskInfo {
    //device info
    public String deviceId;
    public String deviceBrand;
    public String deviceModel;
    public String systemVersion;
    public String appVersionName;
    public int appVersionCode;
    public int appId;
    public String channel;
    //status info
    public Date createTime;
    public long userId;
    public float locationLat;
    public float locationLng;
    public String locationName;
    public String netState;
    public String requestIp;
    //risk info
    public String scene;//使用的场景
    public String riskType;
    public String ext;//使用额外字段标识
    public String riskDetail;



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
    public  void createStartEndDate(){
        if (!StringUtils.isEmpty(duringTime)){
             String[] str=duringTime.split("~");
             if (str.length==2){
                 SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

    public String getScene() {
        return scene;
    }

    public void setScene(String sence) {
        this.scene = sence;
    }
    public String getExt() {
        return ext;
    }
    public void setExt(String ext) {
        this.ext = ext;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
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

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public String getRiskDetail() {
        return riskDetail;
    }

    public void setRiskDetail(String riskDetail) {
        this.riskDetail = riskDetail;
    }

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
}
