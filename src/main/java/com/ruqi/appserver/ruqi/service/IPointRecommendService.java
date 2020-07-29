package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;

/**
 * 推荐上车点的服务
 */
public interface IPointRecommendService {

    /**
     * 采集推荐上车点
     * @param uploadRecommendPointRequest
     */
    BaseCodeMsgBean saveRecommendPoint(UploadRecommendPointRequest uploadRecommendPointRequest);

    /**
     * 查询推荐上车点
     * @param queryRecommendPointRequest
     * @return RecommendPointList<RecommendPoint>
     */
    RecommendPointList<RecommendPoint> queryRecommendPoint(QueryRecommendPointRequest queryRecommendPointRequest);
}
