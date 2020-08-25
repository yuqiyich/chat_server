package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.GeoRecommendRelatedId;
import com.ruqi.appserver.ruqi.geomesa.db.*;
import com.ruqi.appserver.ruqi.geomesa.db.connect.MesaDataConnectManager;
import com.ruqi.appserver.ruqi.geomesa.db.updateListener.RecommendDataUpdater;
import com.ruqi.appserver.ruqi.geomesa.db.updateListener.RecommendPointUpdater;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 推荐上车点的数据处理管理类
 *
 * 数据表都已cityCode作为分表的
 */
public class RPHandleManager {
    private static RPHandleManager ins;
    private static Logger logger = LoggerFactory.getLogger(GeoDbHandler.class);
    public static final String DEV = "dev";//开发环境表名字段
    public static final String PRO = "pro";//正式环境表名字段

    public static RPHandleManager getIns(){
         synchronized (RPHandleManager.class){
             if (ins==null){
                 ins=new RPHandleManager();
             }
         }
         return ins;
    }
    private RPHandleManager(){
    }

    /**
     * 存储记录数据
     *
     * @param evn  存储表的环境
     * @param cityCode  城市编码
     * @param record  记录数据
     */
    public void saveRecommendDatasByCityCode(String evn,String cityCode, UploadRecommendPointRequest<RecommendPoint> record){
        ArrayList<UploadRecommendPointRequest<RecommendPoint>> records=new ArrayList<>();
        records.add(record);
        saveRecommendRecordsByCityCode(evn,cityCode,records);
    }
    public void saveRecommendRecordsByCityCode(String evn,String cityCode,List<UploadRecommendPointRequest<RecommendPoint>> records){
        String tableTail=cityCode;
        if (!StringUtils.isEmpty(evn)){
            tableTail=   evn+"_"+cityCode;
        }
        if (!StringUtils.isEmpty(cityCode)){
            List<String> recordIds = saveRecommendPointsRecords(records,tableTail);//存储记录总表
            saveSelectRecommendPoints(records,tableTail);//存储用户选择点和多个推荐点的表
            saveRecommendPoints(records,recordIds,tableTail);//推荐点记录表
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
        SimpleFeatureType sft=GeoTable.getRecommendRecordSimpleType(cityCode,false);
        List<SimpleFeature> recordsDatas=convertRecommendDataToPointSF(records,sft);
        updateDataIfFidExistOrInsert(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX +cityCode),recordsDatas,sft,GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_RECORD,new RecommendDataUpdater());
    }

    /**
     * 转化为推荐数据sf数据
     * @param records
     * @param sft
     * @return
     */
    private List<SimpleFeature> convertRecommendDataToPointSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records!=null&&!records.isEmpty()){
            int recommendRecordSize=records.size();
            for (int i = 0; i < recommendRecordSize ; i++) {
                datas.add(GeoMesaDataWrapper.convertRecordToRecordSF(records.get(i),sft,false));
            }
        }
        return datas;
    }

    /**
     * 插入推荐点记录数据和推荐点数据关系表
     * @param datas
     * @param citycode
     */
    private void saveRecommendPointsRelated(List<GeoRecommendRelatedId> datas,String citycode) {
        SimpleFeatureType sft=GeoTable.getRecommendRelatedSimpleType(citycode);
        List<SimpleFeature> sfDatas=new ArrayList<>();
        for (GeoRecommendRelatedId geoRecommendRelatedId:datas) {
            sfDatas.add(GeoMesaDataWrapper.convertRecordToRelatedSF(geoRecommendRelatedId,sft));
        }
       insertInDb(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX+citycode),sfDatas,sft);
    }

    private void saveRecommendPoints(List<UploadRecommendPointRequest<RecommendPoint>> records,List<String> recordIds,String cityCode) {
        SimpleFeatureType sft=GeoTable.getRecommendPointSimpleType(cityCode);
        GeoDbHandler.createOrUpdateSchema(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+cityCode), sft);
        List<SimpleFeature> pointsDatas=convertRecommendPointToSF(records,sft,recordIds,cityCode);
        updateDataIfFidExistOrInsert(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+cityCode),pointsDatas,sft,GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_POINT,new RecommendPointUpdater());
    }

    private List<SimpleFeature> convertRecommendPointToSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft,List<String> recordIds,String cityCode) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records!=null&&!records.isEmpty()){
            int recommendRecordSize=records.size();
            List<GeoRecommendRelatedId> relatedIds = new ArrayList<>();
            for (int i = 0; i < recommendRecordSize; i++) {
                if (records.get(i) != null && records.get(i).getRecommendPoint() != null) {
                    List<RecommendPoint> recommendPoints = records.get(i).getRecommendPoint();
                    int recommendPointSize = recommendPoints.size();
                    for (int j = 0; j < recommendPointSize; j++) {
                        SimpleFeature simpleFeature = GeoMesaDataWrapper.convertRecordToPointSF( records.get(i), recommendPoints.get(j), sft);
                        datas.add(simpleFeature);
                        //构造记录关系表数据
                        GeoRecommendRelatedId recommendRelatedId=  new GeoRecommendRelatedId();
                        recommendRelatedId.setPointId(simpleFeature.getID());
                        recommendRelatedId.setRecordId(recordIds.get(i));
                        relatedIds.add(recommendRelatedId);
                    }
                }
            }
            saveRecommendPointsRelated(relatedIds,cityCode);
        }
        return datas;
    }

    /**
     * 存储记录列表
     * @param records
     */
    private List<String> saveRecommendPointsRecords(List<UploadRecommendPointRequest<RecommendPoint>> records,String cityCode){
        SimpleFeatureType sft=GeoTable.getRecommendRecordSimpleType(cityCode,true);
        List<SimpleFeature> recordsDatas=convertRecordToPointSF(records,sft);
        insertInDb(MesaDataConnectManager.getIns().getDataStore(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX+cityCode),recordsDatas,sft);
        List<String>  recordIds=getRecordIDs(recordsDatas);
        return recordIds;
    }

    private List<SimpleFeature> convertRecordToPointSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records!=null&&!records.isEmpty()){
            int recommendRecordSize=records.size();
            for (int i = 0; i < recommendRecordSize ; i++) {
                datas.add(GeoMesaDataWrapper.convertRecordToRecordSF(records.get(i),sft,true));
            }
        }
        return datas;
    }

    private List<String> getRecordIDs(List<SimpleFeature> recordsDatas) {
        List<String> recordIds=new ArrayList<>();
        if (recordsDatas!=null&&!recordsDatas.isEmpty()){
            for (SimpleFeature sfData:recordsDatas) {
                recordIds.add(sfData.getID());
            }
        }
        return  recordIds;
    }

    private List<SimpleFeature> convertRecordsToRelatedSF(List<GeoRecommendRelatedId> records, SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records!=null&&!records.isEmpty()){
            int recommendRecordSize=records.size();
            for (int i = 0; i < recommendRecordSize ; i++) {
                datas.add(GeoMesaDataWrapper.convertRecordToRelatedSF(records.get(i),sft));
            }
        }
        return datas;
    }

    private void insertInDb(DataStore mDataStore, List<SimpleFeature> recordsDatas, SimpleFeatureType sft) {
        GeoDbHandler.createOrUpdateSchema(mDataStore, sft);
        try {
            GeoDbHandler.writeNewFeaturesData(mDataStore,sft,recordsDatas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDataIfFidExistOrInsert(DataStore mDataStore, List<SimpleFeature> recordsDatas, SimpleFeatureType sft, String fidName, GeoDbHandler.IUpdateDataListener iUpdateDataListenerCallback) {
        GeoDbHandler.createOrUpdateSchema(mDataStore, sft);
        GeoDbHandler.updateExistDataOrInsert(mDataStore,sft,recordsDatas,fidName, iUpdateDataListenerCallback);
    }


    public int getTotalUploadTimes(String env) {
       return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX,env);
    }

    public int getLastDayUploadTimes(String env) {
         return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX,getLastDayFilter(),env);
    }

    public Map<String, Integer> getCityLastDayUploadTimes(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX,getLastDayFilter(),env);
    }

    public int getLastDayRecommendDataCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_DATA_PREFIX,getLastDayFilter(),env);
    }

    public Map<String, Integer> getCityLastDayRecommendDataCount(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMEND_DATA_PREFIX,getLastDayFilter(),env);
    }

    public int getTotalRecommendDataCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMEND_DATA_PREFIX,env);
    }

    public int getLastDayRecommendPointCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX,getLastDayFilter(),env);
    }

    public Map<String, Integer> getCityLastDayRecommendPointCount(String env) {
        return queryTableCityCount(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX,getLastDayFilter(),env);
    }

    public int getTotalRecommendPointCount(String env) {
        return queryTableDataCountWithAllCity(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX,env);
    }

    private int queryTableDataCountWithAllCity(String tableNamePrefix,String env){
        return     (int)queryTableDataCountWithAllCity(tableNamePrefix,"",env,false);
    }
    private int queryTableDataCountWithAllCity(String tableNamePrefix,String filter,String env){
       return     (int)queryTableDataCountWithAllCity(tableNamePrefix,filter,env,false);
    }


    /**
     *
     * @param tableNamePrefix 表名前缀  见例如下面的值
     *                               @see GeoTable#TABLE_RECOMMOND_PONIT_PREFIX
     * @param cqlFilter  cql查询语句的
     * @param env  cql查询语句的
     * @return
     */
    public HashMap<String,Integer> queryTableCityCount(String tableNamePrefix,String cqlFilter,String env){
        return  (HashMap<String,Integer>)queryTableDataCountWithAllCity(tableNamePrefix,cqlFilter,env,true);

    }

    /**
     *  返回特定查询条件下的数据数目
     *
     * @param tableNamePrefix  表名前缀  例如下面的值
     *                          @see GeoTable#TABLE_RECOMMOND_PONIT_PREFIX
     * @param cqlFilter cql的语句 like下面的方法
     *                           @see #getLastDayFilter()
     * @param isDetail
     * @return
     */
    private Object queryTableDataCountWithAllCity(String tableNamePrefix,String cqlFilter,String env,boolean isDetail) {
        int counts = 0;
        HashMap<String, Integer> countRecords = new HashMap<>();
        if (!StringUtils.isEmpty(tableNamePrefix)) {
            //todo 目前还根据dev环境写死代码查询，等待改成配置的环境来确定锁查询的表
            List<String> tableNames = HbaseDbHandler.getGeoTableNames(tableNamePrefix + env);
            if (tableNames.size() > 0) {
                for (String name : tableNames) {
                    String cityCode = name.substring(name.lastIndexOf("_"));
                    int count = GeoDbHandler.queryTableRowCount(name, cqlFilter);
                    counts += count;
                    countRecords.put(cityCode, count);
                }
            }
        }
        if (isDetail){
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
    private String getLastDayFilter(){
        return "dtg DURING "+GeoMesaUtil.getGeoMesaTimeStr(DateTimeUtils.getYesterdayStartDate())+"/"+ GeoMesaUtil.getGeoMesaTimeStr(DateTimeUtils.getYesterdayEndDate());
    }


}
