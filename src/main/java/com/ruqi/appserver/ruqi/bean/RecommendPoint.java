package com.ruqi.appserver.ruqi.bean;

/**
 * 推荐上车点实体
 */
public class RecommendPoint {
    private double lat;
    private double lng;
    private String title;
    private String address;
    private double distanc;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistanc() {
        return distanc;
    }

    public void setDistanc(double distanc) {
        this.distanc = distanc;
    }
}

