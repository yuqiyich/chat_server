package com.ruqi.appserver.ruqi.request;

/**
 * Author:liangbingkun
 * Time:2020/7/22
 * Description: 明文请求实体
 */
public class QueryRecommendPointRequest extends BaseRequest{
    private double userLat;
    private double userLng;
    private double selectLat;
    private double selectLng;
    private String coordinateType;
    private String mapSdkType;

    public void setMapSdkType(String mapSdkType) {
        this.mapSdkType = mapSdkType;
    }

    public String getMapSdkType() {
        return mapSdkType;
    }

    public void setCoordinateType(String coordinateType) {
        this.coordinateType = coordinateType;
    }

    public void setSelectLat(double selectLat) {
        this.selectLat = selectLat;
    }

    public void setSelectLng(double selectLng) {
        this.selectLng = selectLng;
    }

    public void setUserLat(double userLat) {
        this.userLat = userLat;
    }

    public void setUserLng(double userLng) {
        this.userLng = userLng;
    }

    public double getSelectLat() {
        return selectLat;
    }

    public double getSelectLng() {
        return selectLng;
    }

    public double getUserLat() {
        return userLat;
    }

    public double getUserLng() {
        return userLng;
    }

    public String getCoordinateType() {
        return coordinateType;
    }
}
