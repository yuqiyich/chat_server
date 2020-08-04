package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PointRecommendServiceImpl implements IPointRecommendService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    AppInfoSevice appInfoSevice;
    @Autowired
    UserMapper userWrapper;
    @Autowired
    RiskInfoWrapper riskInfoWrapper;
    @Autowired
    DotEventInfoWrapper dotEventInfoWrapper;

    @Override
    @Async("taskExecutor")
    public BaseCodeMsgBean saveRecommendPoint(UploadRecommendPointRequest uploadRecommendPointRequest,String evn) {
        BaseCodeMsgBean baseCodeMsgBean = new BaseCodeMsgBean();
        RPHandleManager.getIns().saveRecommendDatasByCityCode(evn,uploadRecommendPointRequest.getCityCode(),(UploadRecommendPointRequest<RecommendPoint>)uploadRecommendPointRequest);
        return baseCodeMsgBean;
    }

    @Override
    public RecommendPointList<RecommendPoint> queryRecommendPoint(QueryRecommendPointRequest queryRecommendPointRequest) {
        return null;
    }

}
