package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.bean.GeoRecommendRelatedId;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.response.PointList;
import com.ruqi.appserver.ruqi.geomesa.db.*;
import com.ruqi.appserver.ruqi.geomesa.db.connect.MesaDataConnectManager;
import com.ruqi.appserver.ruqi.geomesa.db.updateListener.RecommendDataUpdater;
import com.ruqi.appserver.ruqi.geomesa.db.updateListener.RecommendPointUpdater;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.utils.CityUtil;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.WORLD_CODE;

/**
 * 推荐上车点的数据处理管理类
 * <p>
 * 数据表都已cityCode作为分表的
 */
public class RPHandleManager {
    private static RPHandleManager ins;
    private static Logger logger = LoggerFactory.getLogger(RPHandleManager.class);
    public static final String DEV = "dev";//开发环境表名字段
    public static final String PRO = "pro";//正式环境表名字段


    public static RPHandleManager getIns() {
        synchronized (RPHandleManager.class) {
            if (ins == null) {
                ins = new RPHandleManager();
            }
        }
        return ins;
    }

    private RPHandleManager() {
    }

    /**
     * 存储记录数据
     *
     * @param evn      存储表的环境
     * @param cityCode 城市编码
     * @param record   记录数据
     */
    public void saveRecommendDatasByCityCode(String evn, String cityCode, UploadRecommendPointRequest<RecommendPoint> record) {
        ArrayList<UploadRecommendPointRequest<RecommendPoint>> records = new ArrayList<>();
        records.add(record);
        saveRecommendRecordsByCityCode(evn, cityCode, records);
    }

    public void saveRecommendRecordsByCityCode(String evn, String cityCode, List<UploadRecommendPointRequest<RecommendPoint>> records) {
        String tableTail = cityCode;
        if (!StringUtils.isEmpty(evn)) {
            tableTail = evn + "_" + cityCode;
        }
        if (!StringUtils.isEmpty(cityCode)) {
            //目前记录表都是一个表
            List<String> recordIds = saveRecommendPointsRecords(records, evn + "_" + WORLD_CODE);//存储记录总表
            saveSelectRecommendPoints(records, tableTail);//存储用户选择点和多个推荐点的城市分表的
            saveSelectRecommendPoints(records, evn + "_" + WORLD_CODE);//存储用户选择点和多个推荐点的城市未分表的
            saveRecommendPoints(records, recordIds, tableTail);//推荐点记录表
            saveRecommendPoints(records, recordIds, evn + "_" + WORLD_CODE);//推荐点记录表
        } else {
            logger.error("no cityCode,don't save anything");
        }
    }

    /**
     * 存储选择点和推荐点的表
     *
     * @param records
     * @param cityCode
     */
    private void saveSelectRecommendPoints(List<UploadRecommendPointRequest<RecommendPoint>> records, String cityCode) {
        SimpleFeatureType sft = GeoTable.getRecommendRecordSimpleType(cityCode, false);
        List<SimpleFeature> recordsDatas = convertRecommendDataToPointSF(records, sft);
        updateDataIfFidExistOrInsert(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX + cityCode), recordsDatas, sft, GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_RECORD, new RecommendDataUpdater());
    }

    /**
     * 转化为推荐数据sf数据
     *
     * @param records
     * @param sft
     * @return
     */
    private List<SimpleFeature> convertRecommendDataToPointSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records != null && !records.isEmpty()) {
            int recommendRecordSize = records.size();
            for (int i = 0; i < recommendRecordSize; i++) {
                datas.add(GeoMesaDataWrapper.convertRecordToRecordSF(records.get(i), sft, false));
            }
        }
        return datas;
    }

    /**
     * 插入推荐点记录数据和推荐点数据关系表
     *
     * @param datas
     * @param citycode
     */
    private void saveRecommendPointsRelated(List<GeoRecommendRelatedId> datas, String citycode) {
        SimpleFeatureType sft = GeoTable.getRecommendRelatedSimpleType(citycode);
        List<SimpleFeature> sfDatas = new ArrayList<>();
        for (GeoRecommendRelatedId geoRecommendRelatedId : datas) {
            sfDatas.add(GeoMesaDataWrapper.convertRecordToRelatedSF(geoRecommendRelatedId, sft));
        }
        insertInDb(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX + citycode), sfDatas, sft);
    }

    private void saveRecommendPoints(List<UploadRecommendPointRequest<RecommendPoint>> records, List<String> recordIds, String cityCode) {
        SimpleFeatureType sft = GeoTable.getRecommendPointSimpleType(cityCode);
        GeoDbHandler.createOrUpdateSchema(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX + cityCode), sft);
        List<SimpleFeature> pointsDatas = convertRecommendPointToSF(records, sft, recordIds, cityCode);
        updateDataIfFidExistOrInsert(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX + cityCode), pointsDatas, sft, GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_POINT, new RecommendPointUpdater());
    }

    private List<SimpleFeature> convertRecommendPointToSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft, List<String> recordIds, String cityCode) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records != null && !records.isEmpty()) {
            int recommendRecordSize = records.size();
            List<GeoRecommendRelatedId> relatedIds = new ArrayList<>();
            for (int i = 0; i < recommendRecordSize; i++) {
                if (records.get(i) != null && records.get(i).getRecommendPoint() != null) {
                    List<RecommendPoint> recommendPoints = records.get(i).getRecommendPoint();
                    int recommendPointSize = recommendPoints.size();
                    for (int j = 0; j < recommendPointSize; j++) {
                        SimpleFeature simpleFeature = GeoMesaDataWrapper.convertRecordToPointSF(records.get(i), recommendPoints.get(j), sft);
                        datas.add(simpleFeature);
                        if (cityCode.contains(WORLD_CODE)) {
                            //构造记录关系表数据
                            GeoRecommendRelatedId recommendRelatedId = new GeoRecommendRelatedId();
                            recommendRelatedId.setPointId(simpleFeature.getID());
                            recommendRelatedId.setRecordId(recordIds.get(i));
                            relatedIds.add(recommendRelatedId);
                        }
                    }
                }
            }
            if (cityCode.contains(WORLD_CODE)) {
                saveRecommendPointsRelated(relatedIds, cityCode);
            }

        }
        return datas;
    }

    /**
     * 存储记录列表
     *
     * @param records
     */
    private List<String> saveRecommendPointsRecords(List<UploadRecommendPointRequest<RecommendPoint>> records, String cityCode) {
        SimpleFeatureType sft = GeoTable.getRecommendRecordSimpleType(cityCode, true);
        List<SimpleFeature> recordsDatas = convertRecordToPointSF(records, sft);
        insertInDb(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX + cityCode), recordsDatas, sft);
        List<String> recordIds = getRecordIDs(recordsDatas);
        return recordIds;
    }

    private List<SimpleFeature> convertRecordToPointSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records != null && !records.isEmpty()) {
            int recommendRecordSize = records.size();
            for (int i = 0; i < recommendRecordSize; i++) {
                datas.add(GeoMesaDataWrapper.convertRecordToRecordSF(records.get(i), sft, true));
            }
        }
        return datas;
    }

    private List<String> getRecordIDs(List<SimpleFeature> recordsDatas) {
        List<String> recordIds = new ArrayList<>();
        if (recordsDatas != null && !recordsDatas.isEmpty()) {
            for (SimpleFeature sfData : recordsDatas) {
                recordIds.add(sfData.getID());
            }
        }
        return recordIds;
    }

    private List<SimpleFeature> convertRecordsToRelatedSF(List<GeoRecommendRelatedId> records, SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records != null && !records.isEmpty()) {
            int recommendRecordSize = records.size();
            for (int i = 0; i < recommendRecordSize; i++) {
                datas.add(GeoMesaDataWrapper.convertRecordToRelatedSF(records.get(i), sft));
            }
        }
        return datas;
    }

    private void insertInDb(DataStore mDataStore, List<SimpleFeature> recordsDatas, SimpleFeatureType sft) {
        GeoDbHandler.createOrUpdateSchema(mDataStore, sft);
        try {
            GeoDbHandler.writeNewFeaturesData(mDataStore, sft, recordsDatas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDataIfFidExistOrInsert(DataStore mDataStore, List<SimpleFeature> recordsDatas, SimpleFeatureType sft, String fidName, GeoDbHandler.IUpdateDataListener iUpdateDataListenerCallback) {
        GeoDbHandler.createOrUpdateSchema(mDataStore, sft);
        GeoDbHandler.updateExistDataOrInsert(mDataStore, sft, recordsDatas, fidName, iUpdateDataListenerCallback);
    }


    public int getTotalUploadTimes(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, env, true);
    }

    public int getLastDayUploadTimes(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, getLastDayFilter(), env, true);
    }

    public Map<String, Integer> getCityLastDayUploadTimes(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, getLastDayFilter(), env, true);
    }

    public Map<String, Integer> getCityRecommendDataCountBeforeToday(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, getBeforeTodayFilter(), env, false);
    }

    /**
     * 获取到今天之前的上报记录的每个城市总量
     *
     * @param env
     * @return
     */
    public Map<String, Integer> getCityUploadCountBeforeToday(String env) {
        return queryCityDataCountInEarth(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX, getBeforeTodayFilter(), env);
    }

    // 全球表中查询每个城市的数据量
    private Map<String, Integer> queryCityDataCountInEarth(String tableNamePrefix, String cqlFilter, String env) {
        HashMap<String, Integer> countRecords = new HashMap<>();
        if (!StringUtils.isEmpty(tableNamePrefix) && !StringUtils.isEmpty(env)) {
            String tabName = tableNamePrefix + env + "_" + WORLD_CODE;
            try {
                if (HbaseDbHandler.hasTable(tabName)) {
                    countRecords.putAll(GeoDbHandler.queryGroupCount(tabName, cqlFilter, GeoTable.KEY_CITY_CODE));
                } else {
                    logger.error("[" + tabName + " ]has no table in geomesa");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return countRecords;
    }

    public int getLastDayRecommendDataCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, getLastDayFilter(), env, false);
    }

    public Map<String, Integer> getCityLastDayRecommendDataCount(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, getLastDayFilter(), env, false);
    }

    public int getTotalRecommendDataCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_DATA_PREFIX, env, false);
    }

    public int getLastDayRecommendPointCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, getLastDayFilter(), env, false);
    }

    public Map<String, Integer> getCityRecommendPointCountBeforeToday(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, getBeforeTodayFilter(), env, false);
    }

    public Map<String, Integer> getCityLastDayRecommendPointCount(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, getLastDayFilter(), env, false);
    }

    public int getTotalRecommendPointCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX, env, false);
    }

    private int queryTableDataCountWithAllCity(String tableNamePrefix, String env, Boolean can) {
        return (int) queryTableDataCountWithAllCity(tableNamePrefix, "", env, false, can);
    }

    private int queryTableDataCountWithAllCity(String tableNamePrefix, String filter, String env, boolean canEarth) {
        return (int) queryTableDataCountWithAllCity(tableNamePrefix, filter, env, false, canEarth);
    }


    /**
     * @param tableNamePrefix 表名前缀  见例如下面的值
     * @param cqlFilter       cql查询语句的
     * @param env             cql查询语句的
     * @return
     * @see GeoTable#TABLE_RECOMMOND_PONIT_PREFIX
     */
    public HashMap<String, Integer> queryTableCityCount(String tableNamePrefix, String cqlFilter, String env, boolean can) {
        return (HashMap<String, Integer>) queryTableDataCountWithAllCity(tableNamePrefix, cqlFilter, env, true, can);

    }

    /**
     * 返回特定查询条件下的数据数目
     *
     * @param tableNamePrefix 表名前缀  例如下面的值
     * @param cqlFilter       cql的语句 like下面的方法
     * @param isDetail
     * @param canEndWithEarth 是否查全球表的
     * @return
     * @see GeoTable#TABLE_RECOMMOND_PONIT_PREFIX
     * @see #getLastDayFilter()
     */
    private Object queryTableDataCountWithAllCity(String tableNamePrefix, String cqlFilter, String env, boolean isDetail, boolean canEndWithEarth) {
        int counts = 0;
        HashMap<String, Integer> countRecords = new HashMap<>();
        if (!StringUtils.isEmpty(tableNamePrefix)) {
            List<String> tableNames = HbaseDbHandler.getGeoTableNamesWithPrefix(tableNamePrefix + env);
            logger.info("--->tableNames:" + tableNames);
            if (tableNames.size() > 0) {
                for (String name : tableNames) {
                    if (name.endsWith(WORLD_CODE) && !canEndWithEarth) {
                        continue;
                    }
                    String cityCode = name.substring(name.lastIndexOf("_")).replace("_", "");
                    int count = GeoDbHandler.queryTableRowCount(name, cqlFilter);
                    counts += count;
                    countRecords.put(cityCode, count);
                }
            } else {
                logger.error("[" + tableNamePrefix + " ]has no table in geomesa");
            }
        }
        if (isDetail) {
            return countRecords;
        } else {
            return counts;
        }
    }

    /**
     * 获取昨天 一天内的时间段cql
     *
     * @return
     */
    private String getLastDayFilter() {
        return "dtg DURING " + GeoMesaUtil.getGeoMesaTimeStr(DateTimeUtils.getYesterdayStartDate()) + "/" + GeoMesaUtil.getGeoMesaTimeStr(DateTimeUtils.getYesterdayEndDate());
    }

    /**
     * 获取今天前的时间段cql
     *
     * @return
     */
    private String getBeforeTodayFilter() {
        return "dtg DURING 2020-08-01T00:00:00+00:00/" + GeoMesaUtil.getGeoMesaTimeStr(DateTimeUtils.getTodayStartDate());
    }


    /***
     * 查询矩形区域的所有感兴趣的点
     *
     * @param north  北纬度
     * @param east 东经度
     * @param south  南纬度
     * @param west  西经度
     * @param dev  所对应的环境
     * @param tableRecommondPonitPrefix  表名
     * @param sGeom  需要的返回的地理字段
     * @return
     */
    public List<PointList.Point> queryPoints(double north, double east, double south, double west, String dev, String tableRecommondPonitPrefix, String sGeom) {
        List<PointList.Point> points = new ArrayList<>();
        String cqlBox = String.format("BBOX(%s, %s, %s, %s, %s)", sGeom, north,
                east, south, west);
        String fullcql = cqlBox;
        try {
            String tableName = tableRecommondPonitPrefix + dev + "_" + WORLD_CODE;
            if (HbaseDbHandler.hasTable(tableName)) {
                DataStore dataStore = GeoDbHandler.getHbaseTableDataStore(tableName);
                String typeName = MesaDataConnectManager.getIns().getTableTypeName(tableName);
                if (dataStore != null && !StringUtils.isEmpty(typeName)) {
                    List<SimpleFeature> features = GeoDbHandler.queryFeature(dataStore,
                            Arrays.asList(new Query(typeName, ECQL.toFilter(fullcql))));
                    points = convertToPointDatas(features, tableRecommondPonitPrefix, sGeom);
                } else {
                    logger.error("[" + tableName + "] table not exists or schema is null by geomesa");
                }
            } else {
                logger.error("[" + tableName + "] table not exists in hbase");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CQLException e) {
            e.printStackTrace();
        }
        return points;
    }

    // 查询范围内每个区域的数量
    public List<PointList.Point> queryAreaPointCounts(double north, double east, double south, double west, String dev,
                                                      String tableRecommondPonitPrefix, String groupKey, String sGeom) {
        List<PointList.Point> points = new ArrayList<>();
        String cqlBox = String.format("BBOX(%s, %s, %s, %s, %s)", sGeom, north,
                east, south, west);

        int pointType = 0;
        if (MyStringUtils.isEqueals(tableRecommondPonitPrefix, GeoTable.TABLE_RECOMMOND_PONIT_PREFIX)) {
            pointType = 2;
        } else if (MyStringUtils.isEqueals(tableRecommondPonitPrefix, GeoTable.TABLE_RECOMMEND_DATA_PREFIX)) {
            pointType = 1;
        }

        try {
            String tableName = tableRecommondPonitPrefix + dev + "_" + WORLD_CODE;

            if (HbaseDbHandler.hasTable(tableName)) {
                Map<String, Integer> areaPointCountMap = GeoDbHandler.queryGroupCount(tableName, cqlBox, groupKey);
                logger.info("areaPointCountMap:" + areaPointCountMap);
                logger.info("areaPointCountMap.keySet():" + areaPointCountMap.keySet());
                for (String key : areaPointCountMap.keySet()) {
                    // 区域编码后面两位为0的话是城市编码了，应该是错误的，抛弃
                    if (MyStringUtils.isEqueals(groupKey, GeoTable.KEY_AD_CODE) && key.endsWith("00")) {
                        continue;
                    }
                    PointList.Point point = new PointList.Point(CityUtil.getCenterLngLat(key), pointType,
                            CityUtil.getCityName(key), areaPointCountMap.get(key));
                    points.add(point);
                }
            } else {
                logger.error("[" + tableName + "] table not exists in hbase");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return points;
    }

    private List<PointList.Point> convertToPointDatas(List<SimpleFeature> results, String tableRecommondPonitPrefix, String sGeom) {
        List<PointList.Point> points = new ArrayList<>();
        for (SimpleFeature item : results) {
            Point point = (Point) item.getAttribute(sGeom);
            String title = "";
            int pointType = 0;
            String lnglat = "";
            if (MyStringUtils.isEqueals(tableRecommondPonitPrefix, GeoTable.TABLE_RECOMMOND_PONIT_PREFIX)) {
                title = item.getAttribute(GeoTable.KEY_TITLE).toString();
                pointType = 2;
                lnglat = point.getX() + "," + point.getY();
            } else if (MyStringUtils.isEqueals(tableRecommondPonitPrefix, GeoTable.TABLE_RECOMMEND_DATA_PREFIX)) {
                pointType = 1;

                DecimalFormat df = new DecimalFormat("0.0000");
                lnglat = df.format(point.getX()) + "," + df.format(point.getY());
            }
            points.add(new PointList.Point(lnglat, pointType, title));
        }
        return points;
    }
}
