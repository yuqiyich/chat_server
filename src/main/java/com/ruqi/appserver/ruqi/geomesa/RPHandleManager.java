package com.ruqi.appserver.ruqi.geomesa;

import com.aliyuncs.utils.StringUtils;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.GeoRecommendRelatedId;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoMesaDataWrapper;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.geomesa.db.updateListener.RecommendDataUpdater;
import com.ruqi.appserver.ruqi.geomesa.db.updateListener.RecommendPointUpdater;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
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
    private DataStore  mRecordDataStore;//记录表的的store
    private DataStore  mRecommendPointDataStore;//推荐点的store
    private DataStore  mPointRelatedDataStore;//关联点的store
    private DataStore  mRecommendDataStore;//推荐点上报记录的数据store(以selectPoint为主键)

    private static Logger logger = LoggerFactory.getLogger(GeoDbHandler.class);

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
     * @param cityCode  城市编码
     * @param record  记录数据
     */
    public void saveRecommendDatasByCityCode(String cityCode, UploadRecommendPointRequest<RecommendPoint> record){
        ArrayList<UploadRecommendPointRequest<RecommendPoint>> records=new ArrayList<>();
        records.add(record);
        saveRecommendRecordsByCityCode(cityCode,records);
    }
    public void saveRecommendRecordsByCityCode(String cityCode,List<UploadRecommendPointRequest<RecommendPoint>> records){
        if (!StringUtils.isEmpty(cityCode)){
            List<String> recordIds = saveRecommendPointsRecords(records,cityCode);//存储记录总表
            saveSelectRecommendPoints(records,cityCode);//存储用户选择点和多个推荐点的表
            saveRecommendPoints(records,recordIds,cityCode);//推荐点记录表
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
        if (mRecommendDataStore == null) {
            mRecommendDataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX +cityCode);
        }
        SimpleFeatureType sft=GeoTable.getRecommendRecordSimpleType(cityCode,false);
        List<SimpleFeature> recordsDatas=convertRecommendDataToPointSF(records,sft);
        updateDataIfFidExistOrInsert(mRecommendDataStore,recordsDatas,sft,GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_RECORD,new RecommendDataUpdater());
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
        if (mPointRelatedDataStore == null) {
            mPointRelatedDataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX+citycode);
        }
        SimpleFeatureType sft=GeoTable.getRecommendRelatedSimpleType(citycode);
        List<SimpleFeature> sfDatas=new ArrayList<>();
        for (GeoRecommendRelatedId geoRecommendRelatedId:datas) {
            sfDatas.add(GeoMesaDataWrapper.convertRecordToRelatedSF(geoRecommendRelatedId,sft));
        }
       insertInDb(mPointRelatedDataStore,sfDatas,sft);
    }

    private void saveRecommendPoints(List<UploadRecommendPointRequest<RecommendPoint>> records,List<String> recordIds,String cityCode) {
        if (mRecommendPointDataStore == null) {
            mRecommendPointDataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+cityCode);
        }
        SimpleFeatureType sft=GeoTable.getRecommendPointSimpleType(cityCode);
        GeoDbHandler.createSchema(mRecommendPointDataStore, sft);
        List<SimpleFeature> pointsDatas=convertRecommendPointToSF(records,sft,recordIds,cityCode);
        updateDataIfFidExistOrInsert(mRecommendPointDataStore,pointsDatas,sft,GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_POINT,new RecommendPointUpdater());
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
                        SimpleFeature simpleFeature = GeoMesaDataWrapper.convertRecordToPointSF(records.get(i), recommendPoints.get(j), sft);
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
        if (mRecordDataStore == null) {
            mRecordDataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX +cityCode);
        }
        SimpleFeatureType sft=GeoTable.getRecommendRecordSimpleType(cityCode,true);
        List<SimpleFeature> recordsDatas=convertRecordToPointSF(records,sft);
        insertInDb(mRecordDataStore,recordsDatas,sft);
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
        GeoDbHandler.createSchema(mDataStore, sft);
        try {
            GeoDbHandler.writeNewFeaturesData(mDataStore,sft,recordsDatas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDataIfFidExistOrInsert(DataStore mDataStore, List<SimpleFeature> recordsDatas, SimpleFeatureType sft, String fidName, GeoDbHandler.IUpdateDataListener iUpdateDataListenerCallback) {
        GeoDbHandler.createSchema(mDataStore, sft);
        GeoDbHandler.updateExistDataOrInsert(mDataStore,sft,recordsDatas,fidName, iUpdateDataListenerCallback);
    }



}
