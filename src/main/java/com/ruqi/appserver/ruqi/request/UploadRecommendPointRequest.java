package com.ruqi.appserver.ruqi.request;

import java.util.List;

/**
 * Author:liangbingkun
 * Time:2020/7/22
 * Description: 明文请求实体
 */
public class UploadRecommendPointRequest<T> extends BaseRequest{
    private int channel;
    private double selectLat;
    private double selectLng;
    private double userLat;
    private double userLng;
    private String cityCode;
    private String cityName;
    private String adCode;
    private long timeStamp;
    private List<T> recommendPoint;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public double getSelectLat() {
        return selectLat;
    }

    public void setSelectLat(double selectLat) {
        this.selectLat = selectLat;
    }

    public double getSelectLng() {
        return selectLng;
    }

    public void setSelectLng(double selectLng) {
        this.selectLng = selectLng;
    }

    public double getUserLat() {
        return userLat;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public double getUserLng() {
        return userLng;
    }

    public void setUserLng(double userLng) {
        this.userLng = userLng;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<T> getRecommendPoint() {
        return recommendPoint;
    }

    public void setRecommendPoint(List<T> recommendPoint) {
        this.recommendPoint = recommendPoint;
    }
}
