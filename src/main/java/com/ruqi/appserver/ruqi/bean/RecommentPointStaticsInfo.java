package com.ruqi.appserver.ruqi.bean;

import java.util.Date;

/**
 * Author:liangbingkun
 * Time:2020/8/21
 * Description:
 */
public class RecommentPointStaticsInfo {
    private String adCode;
    private String adName;
    private double centerLat;
    private double centerLng;
    /**
     * 所有用户扎针点的推荐上车点的数据量（用户扎针点为索引）
     */
    private int totalOriginPointNum;
    /**
     * 所有的推荐上车点数据量（推荐上车点为索引）
     */
    private int totalRecmdPointNum;
    /**
     * 所有的原始记录数据量（记录为索引）
     */
    private int totalRecordNum;
    private Date staticsDate;
    private String env;

    public void setEnv(String env) {
        this.env = env;
    }

    public String getEnv() {
        return env;
    }

    public void setTotalOriginPointNum(int totalOriginPointNum) {
        this.totalOriginPointNum = totalOriginPointNum;
    }

    public void setTotalRecmdPointNum(int totalRecmdPointNum) {
        this.totalRecmdPointNum = totalRecmdPointNum;
    }

    public void setTotalRecordNum(int totalRecordNum) {
        this.totalRecordNum = totalRecordNum;
    }

    public void setStaticsDate(Date staticsDate) {
        this.staticsDate = staticsDate;
    }

    public int getTotalOriginPointNum() {
        return totalOriginPointNum;
    }

    public int getTotalRecmdPointNum() {
        return totalRecmdPointNum;
    }

    public int getTotalRecordNum() {
        return totalRecordNum;
    }

    public Date getStaticsDate() {
        return staticsDate;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public void setCenterLat(double centerLat) {
        this.centerLat = centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public void setCenterLng(double centerLng) {
        this.centerLng = centerLng;
    }
}
