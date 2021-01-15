package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.response.PointList;
import com.ruqi.appserver.ruqi.bean.response.RecPointDayData;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RecommendPointWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.request.*;
import com.ruqi.appserver.ruqi.utils.CityUtil;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import com.ruqi.appserver.ruqi.utils.ThreadUtils;
import io.micrometer.core.instrument.util.StringUtils;
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
    public RecommendPointList<RecommentPointStaticsInfo> queryStaticsRecommendPoint(QueryStaticRecommendPointsRequest queryStaticRecommendPointsRequest) {
        RecommendPointList<RecommentPointStaticsInfo> recommentPointStaticsInfoRecommendPointList = new RecommendPointList<>();
        List<RecommentPointStaticsInfo> recommentPointStaticsInfoList;
        recommentPointStaticsInfoList = recommendPointWrapper.getRecommendPointLastWeek(queryStaticRecommendPointsRequest.getEnv(), queryStaticRecommendPointsRequest.getCityCode());
        if (null != recommentPointStaticsInfoList && recommentPointStaticsInfoList.size() > 0) {
            for (int i = recommentPointStaticsInfoList.size() - 1; i >= 0; i--) {
                RecommentPointStaticsInfo recommentPointStaticsInfo = recommentPointStaticsInfoList.get(i);
                if (recommentPointStaticsInfo.getAdCode().length() != 6) { // citycode、adcode都应该是6位数
                    recommentPointStaticsInfoList.remove(i);
                } else if (MyStringUtils.isEmpty(CityUtil.getCityName(recommentPointStaticsInfo.getAdCode()))) {
                    recommentPointStaticsInfoList.remove(i);
                }
            }
        }
        // 暂时没维护全国所有的的市区数据，所以有些会临时以未知保存到数据库
        for (RecommentPointStaticsInfo recommentPointStaticsInfo : recommentPointStaticsInfoList) {
            if ((MyStringUtils.isEmpty(recommentPointStaticsInfo.getAdName())
                    || MyStringUtils.isEqueals(recommentPointStaticsInfo.getAdName(), "未知"))
                    && !MyStringUtils.isEmpty(recommentPointStaticsInfo.getAdCode())) {
                recommentPointStaticsInfo.setAdCode(CityUtil.getCityName(recommentPointStaticsInfo.getAdCode()));
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
                recommentPointStaticsInfo.setAdCode(cityCode);
                AreaAdInfo adInfo = baseConfigAreaService.getAreaAdInfo(cityCode);
                if (adInfo != null && !StringUtils.isEmpty(adInfo.getAdName())) {
                    recommentPointStaticsInfo.setAdName(adInfo.getAdName());
                    recommentPointStaticsInfo.setCenterLat(adInfo.getCenterLat());
                    recommentPointStaticsInfo.setCenterLng(adInfo.getCenterLng());
                }
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
                recommentPointStaticsInfo.setAdCode(cityCode);
                AreaAdInfo adInfo = baseConfigAreaService.getAreaAdInfo(cityCode);
                if (adInfo != null && !StringUtils.isEmpty(adInfo.getAdName())) {
                    recommentPointStaticsInfo.setAdName(adInfo.getAdName());
                    recommentPointStaticsInfo.setCenterLat(adInfo.getCenterLat());
                    recommentPointStaticsInfo.setCenterLng(adInfo.getCenterLng());
                }
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

    @Override
    public void staticRecommendPointByAdCode() {
        //截止昨天，上报的原始记录次数
        Map<String, Integer> lastUploadTimesDev = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, DEV);
        Map<String, Integer> lastUploadTimesPro = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, PRO);

        //截止昨天，扎针点和推荐关系表 数量
        Map<String, Integer> lastdayRecommendDataCountDev = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, DEV);
        Map<String, Integer> lastdayRecommendDataCountPro = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, PRO);

        //截止昨天，推荐点数目
        Map<String, Integer> lastDayRecommendPointCountDev = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, DEV);
        Map<String, Integer> lastDayRecommendPointCountPro = RPHandleManager.getIns().staticCountByAdCodeBeforeToday(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, PRO);

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
                recommentPointStaticsInfo.setAdCode(cityCode);
                AreaAdInfo adInfo = baseConfigAreaService.getAreaAdInfo(cityCode);
                if (adInfo != null && !StringUtils.isEmpty(adInfo.getAdName())) {
                    recommentPointStaticsInfo.setAdName(adInfo.getAdName());
                    recommentPointStaticsInfo.setCenterLat(adInfo.getCenterLat());
                    recommentPointStaticsInfo.setCenterLng(adInfo.getCenterLng());
                }
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
                logger.info("adcode插入统计好的DEV数据" + JsonUtil.beanToJsonStr(recommentPointStaticsInfo));
                int result = recommendPointWrapper.insertRecommendPoint(recommentPointStaticsInfo);
                logger.info("adcode插入DEV结果" + result);
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
                recommentPointStaticsInfo.setAdCode(cityCode);
                AreaAdInfo adInfo = baseConfigAreaService.getAreaAdInfo(cityCode);
                if (adInfo != null && !StringUtils.isEmpty(adInfo.getAdName())) {
                    recommentPointStaticsInfo.setAdName(adInfo.getAdName());
                    recommentPointStaticsInfo.setCenterLat(adInfo.getCenterLat());
                    recommentPointStaticsInfo.setCenterLng(adInfo.getCenterLng());
                }
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
                logger.info("adcode插入统计好的PRO数据" + JsonUtil.beanToJsonStr(recommentPointStaticsInfo));
                int result = recommendPointWrapper.insertRecommendPoint(recommentPointStaticsInfo);
                logger.info("adcode插入PRO结果" + result);
            }
        }
    }
}
