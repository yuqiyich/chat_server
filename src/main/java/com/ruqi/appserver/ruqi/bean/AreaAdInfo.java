package com.ruqi.appserver.ruqi.bean;

import java.io.Serializable;

public class AreaAdInfo implements Serializable {

  private String adCode;
  private String adName;
  private double centerLat;
  private double centerLng;
  private String level;

  public String getLevel() {
    return level;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public String getParentAdCode() {
    return parentAdCode;
  }

  public void setParentAdCode(String parentAdCode) {
    this.parentAdCode = parentAdCode;
  }

  private String parentAdCode;

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
