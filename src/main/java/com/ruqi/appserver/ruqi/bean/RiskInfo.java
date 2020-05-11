package com.ruqi.appserver.ruqi.bean;

/**
 * 对应的风险表
 *
 * @author yich
 */
public class RiskInfo extends BaseRecordInfo {
    //risk info
    public String scene;//使用的场景
    public String riskType;
    public String ext;//使用额外字段标识
    public String riskDetail;

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

}
