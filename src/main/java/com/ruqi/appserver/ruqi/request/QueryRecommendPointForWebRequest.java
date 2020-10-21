package com.ruqi.appserver.ruqi.request;

/**
 * @author ZhangYu
 * @date 2020/10/9
 * @desc
 */
public class QueryRecommendPointForWebRequest extends BaseRequest {
    private double selectLat;
    private double selectLng;
    private String env;
    // 模式，默认0表示查询精准推荐，1表示查询数据库中的全部推荐点
    private int mode = 0;

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

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
