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
import com.ruqi.appserver.ruqi.request.*;
import com.ruqi.appserver.ruqi.utils.CityUtil;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import com.ruqi.appserver.ruqi.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    public BaseCodeMsgBean batchSaveRecommendPoint( List<UploadRecommendPointRequest<RecommendPoint>> record,String cityCode, String evn) {
        BaseCodeMsgBean baseCodeMsgBean = new BaseCodeMsgBean();
//        logger.info("jvm 总的线程数："+ ThreadUtils.getAllThreadNum());
        RPHandleManager.getIns().batchSaveRecommendDatasByCityCode(evn,cityCode, record);
        return baseCodeMsgBean;
    }

    @Override
//    @Async("taskExecutor")
    public BaseCodeMsgBean saveRecommendPoint(UploadRecommendPointRequest uploadRecommendPointRequest, String evn) {
        BaseCodeMsgBean baseCodeMsgBean = new BaseCodeMsgBean();
        logger.info("jvm 总的线程数："+ ThreadUtils.getAllThreadNum());
        RPHandleManager.getIns().saveRecommendDatasByCityCode(evn, uploadRecommendPointRequest.getCityCode(), (UploadRecommendPointRequest<RecommendPoint>) uploadRecommendPointRequest);
        return baseCodeMsgBean;
    }

    @Override
    public RecommendPointList<RecommendPoint> queryRecommendPoint(QueryRecommendPointRequest queryRecommendPointRequest, String evn) {
        RecommendPointList recommendPointList = new RecommendPointList();
        recommendPointList.setPointList(RPHandleManager.getIns().queryRecommendPoints(queryRecommendPointRequest.getSelectLng(), queryRecommendPointRequest.getSelectLat(), evn));
        return recommendPointList;
    }

    @Override
    public RecommendPointList<RecommendPoint> queryRecommendPointForWeb(QueryRecommendPointForWebRequest queryRecommendPointForWebRequest, String evn) {
        RecommendPointList recommendPointList = new RecommendPointList();
        recommendPointList.setPointList(RPHandleManager.getIns().queryRecommendPoints(queryRecommendPointForWebRequest.getSelectLng(), queryRecommendPointForWebRequest.getSelectLat(), evn));
        return recommendPointList;
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
        if (null != recommentPointStaticsInfoList && recommentPointStaticsInfoList.size() > 0) {
            for (int i = recommentPointStaticsInfoList.size() - 1; i >= 0; i--) {
                RecommentPointStaticsInfo recommentPointStaticsInfo = recommentPointStaticsInfoList.get(i);
                if (recommentPointStaticsInfo.getCityCode().length() != 6) { // citycode、adcode都应该是6位数
                    recommentPointStaticsInfoList.remove(i);
                } else if (MyStringUtils.isEmpty(CityUtil.getCityName(recommentPointStaticsInfo.getCityCode()))) {
                    recommentPointStaticsInfoList.remove(i);
                }
            }
        }
        // 暂时没维护全国所有的的市区数据，所以有些会临时以未知保存到数据库
        for (RecommentPointStaticsInfo recommentPointStaticsInfo : recommentPointStaticsInfoList) {
            if ((MyStringUtils.isEmpty(recommentPointStaticsInfo.getCityName())
                    || MyStringUtils.isEqueals(recommentPointStaticsInfo.getCityName(), "未知"))
                    && !MyStringUtils.isEmpty(recommentPointStaticsInfo.getCityCode())) {
                recommentPointStaticsInfo.setCityName(CityUtil.getCityName(recommentPointStaticsInfo.getCityCode()));
            }
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
        String dataEnv = queryPointsRequest.getEnvType();

        if (queryPointsRequest.getAreaType() == PointList.TYPE_AREA_POINT) {
            if (queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_ALL ||
                    queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_ORIGIN) {
                GeoDbHandler.setDebug(true);
                dataList.addAll(RPHandleManager.getIns().queryPoints(queryPointsRequest.north, queryPointsRequest.east,
                        queryPointsRequest.south, queryPointsRequest.west, dataEnv,
                        GeoTable.TABLE_RECOMMEND_DATA_PREFIX, GeoTable.KEY_POINT_ORIGIN));
            }
            if (queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_ALL ||
                    queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_RECMD) {
                dataList.addAll(RPHandleManager.getIns().queryPoints(queryPointsRequest.north, queryPointsRequest.east,
                        queryPointsRequest.south, queryPointsRequest.west, dataEnv,
                        GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, GeoTable.KEY_POINT_RECMD));
            }
        } else if (queryPointsRequest.getAreaType() == PointList.TYPE_AREA_DISTRICT
                || queryPointsRequest.getAreaType() == PointList.TYPE_AREA_CITY) {
            boolean isCity = queryPointsRequest.getAreaType() == PointList.TYPE_AREA_CITY;
            if (queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_ALL ||
                    queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_ORIGIN) {
                dataList.addAll(RPHandleManager.getIns().queryAreaPointCounts(queryPointsRequest.north,
                        queryPointsRequest.east, queryPointsRequest.south, queryPointsRequest.west, dataEnv,
                        GeoTable.TABLE_RECOMMEND_DATA_PREFIX, isCity ? GeoTable.KEY_CITY_CODE : GeoTable.KEY_AD_CODE,
                        GeoTable.KEY_POINT_ORIGIN));
            }
            if (queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_ALL ||
                    queryPointsRequest.pointType == QueryPointsRequest.POINT_TYPE_RECMD) {
                dataList.addAll(RPHandleManager.getIns().queryAreaPointCounts(queryPointsRequest.north,
                        queryPointsRequest.east, queryPointsRequest.south, queryPointsRequest.west, dataEnv,
                        GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, isCity ? GeoTable.KEY_CITY_CODE : GeoTable.KEY_AD_CODE,
                        GeoTable.KEY_POINT_RECMD));
            }
        }
        return dataList;
    }

//    @Override
//    public List<PointList.Point> queryPoints(QueryPointsRequest queryPointsRequest) {
//        List<PointList.Point> dataList = new ArrayList<>();
//        if (null == queryPointsRequest) {
//            return dataList;
//        }
//        // 查询全部的城市表的全部数据。暂时固定死查询广州、佛山的。
//        String dataEnv = queryPointsRequest.getEnvType();
//        List<String> cityCodeList = new ArrayList<>();
//        cityCodeList.add("440100"); // 广州
//        cityCodeList.add("440600"); // 佛山
//        String recmdCqlBbox = null;
//        String originCqlBbox = null;
//        if (0 != queryPointsRequest.north && 0 != queryPointsRequest.east && 0 != queryPointsRequest.south && 0 != queryPointsRequest.west) {
//            originCqlBbox = String.format("BBOX(%s, %s, %s, %s, %s)", GeoTable.KEY_POINT_ORIGIN, queryPointsRequest.north,
//                    queryPointsRequest.east, queryPointsRequest.south, queryPointsRequest.west);
//            recmdCqlBbox = String.format("BBOX(%s, %s, %s, %s, %s)", GeoTable.KEY_POINT_RECMD, queryPointsRequest.north,
//                    queryPointsRequest.east, queryPointsRequest.south, queryPointsRequest.west);
//        }
//        boolean needOriginData = false;
//        boolean needRecmdData = false;
//        switch (queryPointsRequest.pointType) {
//            case QueryPointsRequest.POINT_TYPE_ALL:
//                needOriginData = true;
//                needRecmdData = true;
//                break;
//            case QueryPointsRequest.POINT_TYPE_ORIGIN:
//                needOriginData = true;
//                break;
//            case QueryPointsRequest.POINT_TYPE_RECMD:
//                needRecmdData = true;
//                break;
//        }
//        if (needOriginData) {
//            try {
//                for (String cityCode : cityCodeList) {
//                    DataStore originDataStore = GeoDbHandler.getHbaseTableDataStore(
//                            GeoTable.TABLE_RECOMMEND_DATA_PREFIX + dataEnv + "_" + cityCode);
//                    dataList.addAll(queryPointsFromDataStore(originDataStore, PointList.Point.TYPE_POINT_ORIGIN, originCqlBbox));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if (needRecmdData) {
//            try {
//                for (String cityCode : cityCodeList) {
//                    DataStore recmdDataStore = GeoDbHandler.getHbaseTableDataStore(
//                            GeoTable.TABLE_RECOMMOND_PONIT_PREFIX + dataEnv + "_" + cityCode);
//                    dataList.addAll(queryPointsFromDataStore(recmdDataStore, PointList.Point.TYPE_POINT_RECMD, recmdCqlBbox));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return dataList;
//    }

//    private Collection<? extends PointList.Point> queryPointsFromDataStore(DataStore dataStore,
//                                                                           int pointType, String cqlBbox)
//            throws IOException {
//        List<PointList.Point> dataList = new ArrayList<>();
//        List<Query> queries = new ArrayList<>();
//        String[] typeNames = dataStore.getTypeNames();
//        if (null != typeNames && typeNames.length >= 1) {
//            Query query = new Query(typeNames[0]);
////        String dateequal = "dtg DURING 2019-12-31T00:00:00.000Z/2021-10-18T00:00:00.000Z";
////        try {
////            query.setFilter(ECQL.toFilter(dateequal));
////        } catch (CQLException e) {
////            e.printStackTrace();
////        }
//
//            if (!MyStringUtils.isEmpty(cqlBbox)) {
//                try {
//                    query.setFilter(ECQL.toFilter(cqlBbox));
//                } catch (CQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            queries.add(query);
//            List<SimpleFeature> featureList = GeoDbHandler.queryFeature(dataStore, queries);
//            logger.info("featureList size:" + featureList.size());
//            int i = 0;
//            for (SimpleFeature simpleFeature : featureList) {
//                PointList.Point point = new PointList.Point();
//                point.pointType = pointType;
//                if (pointType == PointList.Point.TYPE_POINT_RECMD) {
//                    point.title = simpleFeature.getAttribute(GeoTable.KEY_TITLE).toString();
//                    Point recmdPoint = (Point) simpleFeature.getAttribute(GeoTable.KEY_POINT_RECMD);
//                    point.lnglat = recmdPoint.getCoordinate().x + "," + recmdPoint.getCoordinate().y;
//                } else if (pointType == PointList.Point.TYPE_POINT_ORIGIN) {
//                    Point originPoint = (Point) simpleFeature.getAttribute(GeoTable.KEY_POINT_ORIGIN);
//                    point.lnglat = originPoint.getCoordinate().x + "," + originPoint.getCoordinate().y;
//                }
//
//                dataList.add(point);
////                logger.info("featureList " + i++ + ":" + DataUtilities.encodeFeature(simpleFeature));
//            }
//        }
//        return dataList;
//    }

    @Override
    public void staticRecommendPoint() {
        //截止昨天，上报的原始记录次数
        Map<String, Integer> lastUploadTimesDev = RPHandleManager.getIns().getCityUploadCountBeforeToday(DEV);
        Map<String, Integer> lastUploadTimesPro = RPHandleManager.getIns().getCityUploadCountBeforeToday(PRO);

        //截止昨天，扎针点和推荐关系表 数量
        Map<String, Integer> lastdayRecommendDataCountDev = RPHandleManager.getIns().getCityRecommendDataCountBeforeToday(DEV);
        Map<String, Integer> lastdayRecommendDataCountPro = RPHandleManager.getIns().getCityRecommendDataCountBeforeToday(PRO);

        //截止昨天，推荐点数目
        Map<String, Integer> lastDayRecommendPointCountDev = RPHandleManager.getIns().getCityRecommendPointCountBeforeToday(DEV);
        Map<String, Integer> lastDayRecommendPointCountPro = RPHandleManager.getIns().getCityRecommendPointCountBeforeToday(PRO);

        if (lastUploadTimesDev != null &&
                lastdayRecommendDataCountDev != null &&
                lastDayRecommendPointCountDev != null) {
            for (Map.Entry<String, Integer> entry : lastUploadTimesDev.entrySet()) {
                String cityCode = entry.getKey();
                int uplaodTimesDev = entry.getValue();
                int recommendDataCountDev = 0;
                if (lastdayRecommendDataCountDev.containsKey(cityCode)) {
                    recommendDataCountDev = lastdayRecommendDataCountDev.get(cityCode);
                }
                int recommendPointCountDev = 0;
                if (lastDayRecommendPointCountDev.containsKey(cityCode)) {
                    recommendPointCountDev = lastDayRecommendPointCountDev.get(cityCode);
                }
                RecommentPointStaticsInfo recommentPointStaticsInfo = new RecommentPointStaticsInfo();
                recommentPointStaticsInfo.setCityCode(cityCode);
                recommentPointStaticsInfo.setCityName(CityUtil.getCityName(cityCode));
                recommentPointStaticsInfo.setEnv(DEV);
                recommentPointStaticsInfo.setTotalRecmdPointNum(recommendPointCountDev);
                recommentPointStaticsInfo.setTotalOriginPointNum(recommendDataCountDev);
                recommentPointStaticsInfo.setTotalRecordNum(uplaodTimesDev);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
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
                int recommendDataCountPro = 0;
                if (lastdayRecommendDataCountPro.containsKey(cityCode)) {
                    recommendDataCountPro = lastdayRecommendDataCountPro.get(cityCode);
                }
                int recommendPointCountPro = 0;
                if (lastDayRecommendPointCountPro.containsKey(cityCode)) {
                    recommendPointCountPro = lastDayRecommendPointCountPro.get(cityCode);
                }
                RecommentPointStaticsInfo recommentPointStaticsInfo = new RecommentPointStaticsInfo();
                recommentPointStaticsInfo.setCityCode(cityCode);
                recommentPointStaticsInfo.setCityName(CityUtil.getCityName(cityCode));
                recommentPointStaticsInfo.setEnv(PRO);
                recommentPointStaticsInfo.setTotalRecmdPointNum(recommendPointCountPro);
                recommentPointStaticsInfo.setTotalOriginPointNum(recommendDataCountPro);
                recommentPointStaticsInfo.setTotalRecordNum(uplaodTimesPro);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date time = cal.getTime();
                recommentPointStaticsInfo.setStaticsDate(time);
                logger.info("插入统计好的PRO数据" + JsonUtil.beanToJsonStr(recommentPointStaticsInfo));
                int result = recommendPointWrapper.insertRecommendPoint(recommentPointStaticsInfo);
                logger.info("插入PRO结果" + result);
            }
        }

    }
}
