package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.BaseCodeMsgBean;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.RecommendPointList;
import com.ruqi.appserver.ruqi.bean.RecommentPointStaticsInfo;
import com.ruqi.appserver.ruqi.bean.response.PointList;
import com.ruqi.appserver.ruqi.request.QueryPointsRequest;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.QueryStaticRecommendPointsRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;

import java.util.List;

/**
 * 推荐上车点的服务
 */
public interface IPointRecommendService {

    /**
     * 采集推荐上车点
     *
     * @param uploadRecommendPointRequest
     */
    BaseCodeMsgBean saveRecommendPoint(UploadRecommendPointRequest uploadRecommendPointRequest, String appId);

    /**
     * 查询推荐上车点
     *
     * @param queryRecommendPointRequest
     * @return RecommendPointList<RecommendPoint>
     */
    RecommendPointList<RecommendPoint> queryRecommendPoint(QueryRecommendPointRequest queryRecommendPointRequest);

    /**
     * 查询统计推荐上车点
     *
     * @param queryStaticRecommendPointsRequest
     * @return RecommendPointList<RecommendPoint>
     */
    RecommendPointList<RecommentPointStaticsInfo> queryStaticsRecommendPoint(QueryStaticRecommendPointsRequest queryStaticRecommendPointsRequest);

    /**
     * 查询点的数据。指定环境、点类型、地图范围。
     *
     * @return
     */
    List<PointList.Point> queryPoints(QueryPointsRequest queryPointsRequest);

    void staticRecommendPoint();
}
