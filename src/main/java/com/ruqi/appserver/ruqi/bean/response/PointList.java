package com.ruqi.appserver.ruqi.bean.response;

import java.util.List;

/**
 * @author ZhangYu
 * @date 2020/8/20
 * @desc 接口返回的查询点位数据
 */
public class PointList {
    public static final int TYPE_AREA_POINT = 1;

    // 1 默认，具体坐标点 2 区级 3 市级
    public int areaType;
    public List<Point> points;

    public static class Point {
        public Point() {
        }

        public static final int TYPE_POINT_ORIGIN = 1;
        public static final int TYPE_POINT_RECMD = 2;

        public String lnglat;
        public String title;
        // 1 原始点 2 推荐点
        public int pointType;
    }
}

