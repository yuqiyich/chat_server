package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.response.EventStaticsDataRecPoint;
import com.ruqi.appserver.ruqi.bean.response.PointList;
import com.ruqi.appserver.ruqi.bean.response.RecPointDayData;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RecommendPointWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.request.*;
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

    @Autowired
    BaseConfigAreaService baseConfigAreaService;

    @Override
    @Async("taskExecutor")
    public BaseCodeMsgBean batchSaveRecommendPoint(List<UploadRecommendPointRequest<RecommendPoint>> record, String cityCode, String evn) {
        BaseCodeMsgBean baseCodeMsgBean = new BaseCodeMsgBean();
//        logger.info("jvm 总的线程数："+ ThreadUtils.getAllThreadNum());
        RPHandleManager.getIns().batchSaveRecommendDatasByCityCode(evn, cityCode, record);
        return baseCodeMsgBean;
    }

    @Override
//    @Async("taskExecutor")
    public BaseCodeMsgBean saveRecommendPoint(UploadRecommendPointRequest uploadRecommendPointRequest, String evn) {
        BaseCodeMsgBean baseCodeMsgBean = new BaseCodeMsgBean();
        logger.info("jvm 总的线程数：" + ThreadUtils.getAllThreadNum());
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
    public List<RecPointDayData> queryDayStaticsRecPointDatas(QueryDayStaticRecPointDatasRequest queryDayStaticRecPointDatasRequest) {
        // 初始化要返回的结构体、日期
        if (null == queryDayStaticRecPointDatasRequest) {
            return null;
        }
        List<RecPointDayData> resultList = dotEventInfoWrapper.queryDayStaticsRecPointDatas(queryDayStaticRecPointDatasRequest.env);
        return resultList;
    }

    @Override
    public EventStaticsDataRecPoint queryStaticsRecPointDatas(QueryDayStaticRecPointDatasRequest queryDayStaticRecPointDatasRequest) {
        if (null == queryDayStaticRecPointDatasRequest) {
            return null;
        }
        return dotEventInfoWrapper.queryStaticsRecPointDatas(queryDayStaticRecPointDatasRequest.env);
    }

    @Override
    public RecommendPointList<RecommentPointStaticsInfo> queryStaticsRecommendPoint(QueryStaticRecommendPointsRequest queryStaticRecommendPointsRequest) {
        RecommendPointList<RecommentPointStaticsInfo> recommentPointStaticsInfoRecommendPointList = new RecommendPointList<>();
        List<RecommentPointStaticsInfo> recommentPointStaticsInfoList = recommendPointWrapper.getRecommendPointLastWeek(queryStaticRecommendPointsRequest.getEnv(), queryStaticRecommendPointsRequest.getCityCode());
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
            dataList.addAll(convertStaticBeanToPointBean(getStaticBean(queryPointsRequest.getEnvType(), isCity, queryPointsRequest.north,
                    queryPointsRequest.east, queryPointsRequest.south, queryPointsRequest.west)));
        }
        return dataList;
    }

    private List<PointList.Point> convertStaticBeanToPointBean(List<RecommentPointStaticsInfo> staticBean) {
        List<PointList.Point> results = new ArrayList<>();
        if (staticBean != null && !staticBean.isEmpty()) {
            for (RecommentPointStaticsInfo bean : staticBean) {
                PointList.Point item = new PointList.Point(bean.getCenterLng() + "," + bean.getCenterLat(),
                        bean.getAdName(), bean.getAdCode(), bean.getTotalRecordNum(), bean.getTotalOriginPointNum(),
                        bean.getTotalRecmdPointNum());
                results.add(item);
            }
        }
        return results;
    }

    // 指定市还是区，上下左右坐标范围查询数据
    private List<RecommentPointStaticsInfo> getStaticBean(String env, boolean isCity, double north,
                                                          double east, double south, double west) {
        StringBuilder adCodeWhereCause = new StringBuilder();
        adCodeWhereCause.append("(");
        if (isCity) {
            adCodeWhereCause.append("ad_code LIKE '%00'");
        } else {
            adCodeWhereCause.append("ad_code NOT LIKE '%00'");
        }
        adCodeWhereCause.append(String.format(" AND center_lng > %s AND center_lng < %s AND center_lat > %s AND center_lat < %s",
                south, north, west, east));
        adCodeWhereCause.append(")");
        return recommendPointWrapper.getRecmdPointStaticBeforeToday(env, adCodeWhereCause.toString());
    }

    @Override
    public void staticRecommendPointByAdCode() {
        List<RecommentPointStaticsInfo> recommentPointStaticsInfoList = new LinkedList<>();
        calcDataByKey(recommentPointStaticsInfoList, GeoTable.KEY_AD_CODE);
        calcDataByKey(recommentPointStaticsInfoList, GeoTable.KEY_CITY_CODE);

        logger.info("adcode插入统计好的数据size=" + recommentPointStaticsInfoList.size());
        int result = recommendPointWrapper.insertRecommendPoint(recommentPointStaticsInfoList);
        logger.info("adcode插入结果" + result);
    }

    private void calcDataByKey(List<RecommentPointStaticsInfo> recommentPointStaticsInfoList, String key) {
        //截止昨天，上报的原始记录次数
        Map<String, Integer> devCityCodeRecordMap = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, DEV, key);
        Map<String, Integer> proCityCodeRecordMap = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, PRO, key);
        //截止昨天，扎针点和推荐关系表 数量
        Map<String, Integer> devCityCodeSelectMap = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, DEV, key);
        Map<String, Integer> proCityCodeSelectMap = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, PRO, key);
        //截止昨天，推荐点数目
        Map<String, Integer> devCityCodeRecmdMap = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, DEV, key);
        Map<String, Integer> proCityCodeRecmdMap = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, PRO, key);
        recommentPointStaticsInfoList.addAll(convertStaticsData(RPHandleManager.DEV, devCityCodeRecordMap, devCityCodeSelectMap, devCityCodeRecmdMap));
        recommentPointStaticsInfoList.addAll(convertStaticsData(RPHandleManager.PRO, proCityCodeRecordMap, proCityCodeSelectMap, proCityCodeRecmdMap));
    }

    private List<RecommentPointStaticsInfo> convertStaticsData(String env, Map<String, Integer> recordMap, Map<String, Integer> selectPointMap,
                                                               Map<String, Integer> recmdPointMap) {
        List<RecommentPointStaticsInfo> recommentPointStaticsInfoList = new LinkedList<>();
        if (recordMap != null && selectPointMap != null && recmdPointMap != null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date time = cal.getTime();
            for (Map.Entry<String, Integer> entry : recordMap.entrySet()) {
                String cityCode = entry.getKey();
                RecommentPointStaticsInfo recommentPointStaticsInfo = new RecommentPointStaticsInfo();
                recommentPointStaticsInfo.setAdCode(cityCode);
                AreaAdInfo adInfo = baseConfigAreaService.getAreaAdInfo(cityCode);
                if (adInfo != null) {
                    recommentPointStaticsInfo.setAdName(adInfo.getAdName());
                    recommentPointStaticsInfo.setCenterLat(adInfo.getCenterLat());
                    recommentPointStaticsInfo.setCenterLng(adInfo.getCenterLng());
                }
                recommentPointStaticsInfo.setEnv(env);
                recommentPointStaticsInfo.setTotalRecordNum(entry.getValue());
                if (selectPointMap.containsKey(cityCode)) {
                    recommentPointStaticsInfo.setTotalOriginPointNum(selectPointMap.get(cityCode));
                }
                if (recmdPointMap.containsKey(cityCode)) {
                    recommentPointStaticsInfo.setTotalRecmdPointNum(recmdPointMap.get(cityCode));
                }
                recommentPointStaticsInfo.setStaticsDate(time);
                recommentPointStaticsInfoList.add(recommentPointStaticsInfo);
            }
        }
        return recommentPointStaticsInfoList;
    }
}
