package com.ruqi.appserver.ruqi.bean;

import java.io.Serializable;

public class UploadAppInfo implements Serializable {

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    private String appKey;

    @Override
    public String toString() {
        return "UploadAppInfo{" +
                "appKey='" + appKey + '\'' +
                '}';
    }
}
