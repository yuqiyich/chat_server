package com.ruqi.appserver.ruqi.request;

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

    @ApiModelProperty(value = "环境类型，1：正式 pro 2：测试 dev")
    public int envType = 2;
    @ApiModelProperty(value = "点位类型，1：全部 2：原始点 3：推荐点")
    public int pointType = 1;
    @ApiModelProperty(value = "地图缩放比例 高德地图：1-17")
    public int mapZoom;

    public String getEnvType() {
        if (envType == 2) {
            return "dev";
        } else {
            return "pro";
        }
    }
}
