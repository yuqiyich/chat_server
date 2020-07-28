package com.ruqi.appserver.ruqi.bean;

import java.util.List;

/**
 * 推荐上车点实体
 */
public class RecommendPointList<T> {
    private List<T> pointList;

    public void setPointList(List<T> pointList) {
        this.pointList = pointList;
    }

    public List<T> getPointList() {
        return pointList;
    }
}

