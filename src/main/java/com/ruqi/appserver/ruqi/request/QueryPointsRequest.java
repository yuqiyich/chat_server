package com.ruqi.appserver.ruqi.request;

import com.ruqi.appserver.ruqi.bean.response.PointList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author ZhangYu
 * @date 2020/8/20
 * @desc 查询推荐上车点的点位数据
 */
@ApiModel
public class QueryPointsRequest extends BaseRequest {
    public static final int POINT_TYPE_ALL = 1;
    public static final int POINT_TYPE_ORIGIN = 2;
    public static final int POINT_TYPE_RECMD = 3;

    public static final String ENV_TYPE_DEV = "dev";
    public static final String ENV_TYPE_PRO = "pro";

    @ApiModelProperty(value = "环境类型，默认2。1：正式 pro 2：测试 dev")
    public int envType = 2;
    @ApiModelProperty(value = "点位类型，默认1。1：全部 2：原始点 3：推荐点")
    public int pointType = 1;
    @ApiModelProperty(value = "地图缩放比例 默认10。高德地图：3-18。3：国，7：省，11：市，17：街")
    public int mapZoom = 11;
    public double north;
    public double east;
    public double south;
    public double west;

    public String getEnvType() {
        if (envType == 2) {
            return ENV_TYPE_DEV;
        } else {
            return ENV_TYPE_PRO;
        }
    }

    public int getAreaType() {
        if (mapZoom >= 14) {
            return PointList.TYPE_AREA_POINT;
        } else if (mapZoom >= 9) {
            return PointList.TYPE_AREA_DISTRICT;
        } else {
            return PointList.TYPE_AREA_CITY;
        }
    }
}
