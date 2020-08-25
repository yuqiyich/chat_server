package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.BaseCodeMsgBean;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.RecommendPointList;
import com.ruqi.appserver.ruqi.bean.RecommentPointStaticsInfo;
import com.ruqi.appserver.ruqi.bean.response.PointList;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RecommendPointWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.request.QueryPointsRequest;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.QueryStaticRecommendPointsRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.utils.CityUtil;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.ruqi.appserver.ruqi.geomesa.RPHandleManager.DEV;
import static com.ruqi.appserver.ruqi.geomesa.RPHandleManager.PRO;

@Service
public class PointRecommendServiceImpl implements IPointRecommendService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    AppInfoSevice appInfoSevice;
    @Autowired
    UserMapper userWrapper;
    @Autowired
    RecommendPointWrapper recommendPointWrapper;
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
    public RecommendPointList<RecommentPointStaticsInfo> queryStaticsRecommendPoint(QueryStaticRecommendPointsRequest queryStaticRecommendPointsRequest) {
        RecommendPointList<RecommentPointStaticsInfo> recommentPointStaticsInfoRecommendPointList = new RecommendPointList<>();
        List<RecommentPointStaticsInfo> recommentPointStaticsInfoList;
        if (MyStringUtils.isEmpty(queryStaticRecommendPointsRequest.getCityCode())) {
            recommentPointStaticsInfoList = recommendPointWrapper.getRecmdPointLastWeek(queryStaticRecommendPointsRequest.getEnv());
        } else {
            recommentPointStaticsInfoList = recommendPointWrapper.getRecommendPointLastWeek(queryStaticRecommendPointsRequest.getEnv(), queryStaticRecommendPointsRequest.getCityCode());
        }
        recommentPointStaticsInfoRecommendPointList.setPointList(recommentPointStaticsInfoList);
        return recommentPointStaticsInfoRecommendPointList;
    }

    @Override
    public List<PointList.Point> queryPoints(QueryPointsRequest queryPointsRequest) {
        List<PointList.Point> dataList = new ArrayList<>();
        if (null == queryPointsRequest) {
            return dataList;
        }
        // TODO: 2020/8/20 查询全部的城市表的全部数据。暂时固定死查询广州、佛山的。
        String dataEnv = queryPointsRequest.getEnvType();
        List<String> cityCodeList = new ArrayList<>();
        cityCodeList.add("440100"); // 广州
        cityCodeList.add("440600"); // 佛山
        String recmdCqlBbox = null;
        String originCqlBbox = null;
        if (0 != queryPointsRequest.north && 0 != queryPointsRequest.east && 0 != queryPointsRequest.south && 0 != queryPointsRequest.west) {
            originCqlBbox = String.format("BBOX(%s, %s, %s, %s, %s)", GeoTable.KEY_POINT_ORIGIN, queryPointsRequest.north,
                    queryPointsRequest.east, queryPointsRequest.south, queryPointsRequest.west);
            recmdCqlBbox = String.format("BBOX(%s, %s, %s, %s, %s)", GeoTable.KEY_POINT_RECMD, queryPointsRequest.north,
                    queryPointsRequest.east, queryPointsRequest.south, queryPointsRequest.west);
        }
        boolean needOriginData = false;
        boolean needRecmdData = false;
        switch (queryPointsRequest.pointType) {
            case QueryPointsRequest.POINT_TYPE_ALL:
                needOriginData = true;
                needRecmdData = true;
                break;
            case QueryPointsRequest.POINT_TYPE_ORIGIN:
                needOriginData = true;
                break;
            case QueryPointsRequest.POINT_TYPE_RECMD:
                needRecmdData = true;
                break;
        }
        if (needOriginData) {
            try {
                for (String cityCode : cityCodeList) {
                    DataStore originDataStore = GeoDbHandler.getHbaseTableDataStore(
                            GeoTable.TABLE_RECOMMEND_DATA_PREFIX + dataEnv + "_" + cityCode);
                    dataList.addAll(queryPointsFromDataStore(originDataStore, PointList.Point.TYPE_POINT_ORIGIN, originCqlBbox));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (needRecmdData) {
            try {
                for (String cityCode : cityCodeList) {
                    DataStore recmdDataStore = GeoDbHandler.getHbaseTableDataStore(
                            GeoTable.TABLE_RECOMMOND_PONIT_PREFIX + dataEnv + "_" + cityCode);
                    dataList.addAll(queryPointsFromDataStore(recmdDataStore, PointList.Point.TYPE_POINT_RECMD, recmdCqlBbox));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dataList;
    }

    private Collection<? extends PointList.Point> queryPointsFromDataStore(DataStore dataStore,
                                                                           int pointType, String cqlBbox)
            throws IOException {
        List<PointList.Point> dataList = new ArrayList<>();
        List<Query> queries = new ArrayList<>();
        String[] typeNames = dataStore.getTypeNames();
        if (null != typeNames && typeNames.length >= 1) {
            Query query = new Query(typeNames[0]);
//        String dateequal = "dtg DURING 2019-12-31T00:00:00.000Z/2021-10-18T00:00:00.000Z";
//        try {
//            query.setFilter(ECQL.toFilter(dateequal));
//        } catch (CQLException e) {
//            e.printStackTrace();
//        }

            if (!MyStringUtils.isEmpty(cqlBbox)) {
                try {
                    query.setFilter(ECQL.toFilter(cqlBbox));
                } catch (CQLException e) {
                    e.printStackTrace();
                }
            }

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
//                logger.info("featureList " + i++ + ":" + DataUtilities.encodeFeature(simpleFeature));
            }
        }
        return dataList;
    }

    @Override
    public void staticRecommendPoint() {
        //昨日天上报的原始记录次数
        Map<String, Integer> lastUploadTimesDev = RPHandleManager.getIns().getCityLastDayUploadTimes(DEV);
        Map<String, Integer> lastUploadTimesPro = RPHandleManager.getIns().getCityLastDayUploadTimes(PRO);
        //一天新增的扎针点和推荐关系表
        Map<String, Integer> lastdayRecommendDataCountDev = RPHandleManager.getIns().getCityLastDayRecommendDataCount(DEV);
        Map<String, Integer> lastdayRecommendDataCountPro = RPHandleManager.getIns().getCityLastDayRecommendDataCount(PRO);
        //一天新增的推荐点数目
        Map<String, Integer> lastDayRecommendPointCountDev = RPHandleManager.getIns().getCityLastDayRecommendPointCount(DEV);
        Map<String, Integer> lastDayRecommendPointCountPro = RPHandleManager.getIns().getCityLastDayRecommendPointCount(PRO);

        if (lastUploadTimesDev != null &&
                lastdayRecommendDataCountDev != null &&
                lastDayRecommendPointCountDev != null) {
            for (Map.Entry<String, Integer> entry : lastUploadTimesDev.entrySet()) {
                String cityCode = entry.getKey();
                int uplaodTimesDev = entry.getValue();
                int recommendDataCountDev = lastdayRecommendDataCountDev.get(cityCode);
                int recommendPointCountDev = lastDayRecommendPointCountDev.get(cityCode);
                RecommentPointStaticsInfo recommentPointStaticsInfo = new RecommentPointStaticsInfo();
                recommentPointStaticsInfo.setCityCode(cityCode);
                recommentPointStaticsInfo.setCityName(CityUtil.getCityName(cityCode));
                recommentPointStaticsInfo.setEnv(DEV);
                recommentPointStaticsInfo.setTotalRecmdPointNum(uplaodTimesDev);
                recommentPointStaticsInfo.setTotalOriginPointNum(recommendPointCountDev);
                recommentPointStaticsInfo.setTotalRecordNum(recommendDataCountDev);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date time = cal.getTime();
                recommentPointStaticsInfo.setStaticsDate(time);
                logger.info("插入统计好的DEV数据" + JsonUtil.beanToJsonStr(recommentPointStaticsInfo));
                int result = recommendPointWrapper.insertRecommendPoint(recommentPointStaticsInfo);
                logger.info("插入DEV结果" + result);
            }
        }

        if (lastUploadTimesPro != null &&
                lastdayRecommendDataCountPro != null &&
                lastDayRecommendPointCountPro != null) {
            for (Map.Entry<String, Integer> entry : lastUploadTimesPro.entrySet()) {
                String cityCode = entry.getKey();
                int uplaodTimesPro = entry.getValue();
                int recommendDataCountPro = lastdayRecommendDataCountPro.get(cityCode);
                int recommendPointCountPro = lastDayRecommendPointCountPro.get(cityCode);
                RecommentPointStaticsInfo recommentPointStaticsInfo = new RecommentPointStaticsInfo();
                recommentPointStaticsInfo.setCityCode(cityCode);
                recommentPointStaticsInfo.setCityName(CityUtil.getCityName(cityCode));
                recommentPointStaticsInfo.setEnv(DEV);
                recommentPointStaticsInfo.setTotalRecmdPointNum(uplaodTimesPro);
                recommentPointStaticsInfo.setTotalOriginPointNum(recommendPointCountPro);
                recommentPointStaticsInfo.setTotalRecordNum(recommendDataCountPro);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date time = cal.getTime();
                recommentPointStaticsInfo.setStaticsDate(time);
                logger.info("插入统计好的PRO数据" + JsonUtil.beanToJsonStr(recommentPointStaticsInfo));
                int result = recommendPointWrapper.insertRecommendPoint(recommentPointStaticsInfo);
                logger.info("插入PRO结果" + result);
            }
        }

    }
}
