package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.BaseCodeMsgBean;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.RecommendPointList;
import com.ruqi.appserver.ruqi.bean.response.PointList;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.request.QueryPointsRequest;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public BaseCodeMsgBean saveRecommendPoint(UploadRecommendPointRequest uploadRecommendPointRequest, String evn) {
        BaseCodeMsgBean baseCodeMsgBean = new BaseCodeMsgBean();
        RPHandleManager.getIns().saveRecommendDatasByCityCode(evn, uploadRecommendPointRequest.getCityCode(), (UploadRecommendPointRequest<RecommendPoint>) uploadRecommendPointRequest);
        return baseCodeMsgBean;
    }

    @Override
    public RecommendPointList<RecommendPoint> queryRecommendPoint(QueryRecommendPointRequest queryRecommendPointRequest) {
        return null;
    }

    @Override
    public List<PointList.Point> queryPoints(QueryPointsRequest queryPointsRequest) {
        List<PointList.Point> dataList = new ArrayList<>();
        if (null == queryPointsRequest) {
            return dataList;
        }
        // TODO: 2020/8/20 查询全部的城市表的全部数据。暂时固定死查询广州的。
        String dataEnv = queryPointsRequest.getEnvType();
        String cityCode = "440100";
        switch (queryPointsRequest.pointType) {
            case QueryPointsRequest.POINT_TYPE_ALL:
                try {
                    DataStore recmdDataStore = GeoDbHandler.getHbaseTableDataStore(
                            GeoTable.TABLE_RECOMMOND_PONIT_PREFIX + dataEnv + "_" + cityCode);
                    dataList.addAll(queryPointsFromDataStore(recmdDataStore, PointList.Point.TYPE_POINT_RECMD));


                    DataStore originDataStore = GeoDbHandler.getHbaseTableDataStore(
                            GeoTable.TABLE_RECOMMEND_DATA_PREFIX + dataEnv + "_" + cityCode);
                    dataList.addAll(queryPointsFromDataStore(originDataStore, PointList.Point.TYPE_POINT_ORIGIN));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case QueryPointsRequest.POINT_TYPE_ORIGIN:
                try {
                    DataStore originDataStore = GeoDbHandler.getHbaseTableDataStore(
                            GeoTable.TABLE_RECOMMEND_DATA_PREFIX + dataEnv + "_" + cityCode);
                    dataList.addAll(queryPointsFromDataStore(originDataStore, PointList.Point.TYPE_POINT_ORIGIN));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case QueryPointsRequest.POINT_TYPE_RECMD:
                try {
                    DataStore recmdDataStore = GeoDbHandler.getHbaseTableDataStore(
                            GeoTable.TABLE_RECOMMOND_PONIT_PREFIX + dataEnv + "_" + cityCode);
                    dataList.addAll(queryPointsFromDataStore(recmdDataStore, PointList.Point.TYPE_POINT_RECMD));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return dataList;
    }

    private Collection<? extends PointList.Point> queryPointsFromDataStore(DataStore dataStore, int pointType) throws IOException {
        List<PointList.Point> dataList = new ArrayList<>();
        List<Query> queries = new ArrayList<>();
        String[] typeNames = dataStore.getTypeNames();
        Query query = new Query(typeNames[0]);
//        String dateequal = "dtg DURING 2019-12-31T00:00:00.000Z/2021-10-18T00:00:00.000Z";
//        try {
//            query.setFilter(ECQL.toFilter(dateequal));
//        } catch (CQLException e) {
//            e.printStackTrace();
//        }

        queries.add(query);
        List<SimpleFeature> featureList = GeoDbHandler.queryFeature(dataStore, queries);
        logger.info("featureList size:" + featureList.size());
        int i = 0;
        for (SimpleFeature simpleFeature : featureList) {
            PointList.Point point = new PointList.Point();
            point.pointType = pointType;
            if (pointType == PointList.Point.TYPE_POINT_RECMD) {
                point.title = simpleFeature.getAttribute(GeoTable.KEY_TITLE).toString();
                Point recmdPoint = (Point) simpleFeature.getAttribute(GeoTable.KEY_POINT_RECMD);
                point.lnglat = recmdPoint.getCoordinate().x + "," + recmdPoint.getCoordinate().y;
            } else if (pointType == PointList.Point.TYPE_POINT_ORIGIN) {
                Point originPoint = (Point) simpleFeature.getAttribute(GeoTable.KEY_POINT_ORIGIN);
                point.lnglat = originPoint.getCoordinate().x + "," + originPoint.getCoordinate().y;
            }

            dataList.add(point);
            logger.info("featureList " + i++ + ":" + DataUtilities.encodeFeature(simpleFeature));
        }
        return dataList;
    }

}
