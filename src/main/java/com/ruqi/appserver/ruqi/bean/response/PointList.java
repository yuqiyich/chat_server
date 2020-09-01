package com.ruqi.appserver.ruqi.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author ZhangYu
 * @date 2020/8/20
 * @desc 接口返回的查询点位数据
 */
@ApiModel
public class PointList {
    public static final int TYPE_AREA_POINT = 1;
    public static final int TYPE_AREA_DISTRICT = 2;
    public static final int TYPE_AREA_CITY = 3;

    @ApiModelProperty(value = "点位区域类型，1：具体坐标点 2：区级 3：市级")
    public int areaType;
    public List<Point> points;

    public static class Point {
        public Point() {
        }

        public Point(String x, int type, String title) {
            lnglat = x;
            pointType = type;
            this.title = title;
        }

        public Point(String x, int type, String title, int pointCount) {
            lnglat = x;
            pointType = type;
            this.title = title;
            this.pointCount = pointCount;
        }

        public static final int TYPE_POINT_ORIGIN = 1;
        public static final int TYPE_POINT_RECMD = 2;

        public String lnglat;
        public String title;
        @ApiModelProperty(value = "点位类型，1：原始点 2：推荐点")
        public int pointType;
        public int pointCount = -1;
    }
}

