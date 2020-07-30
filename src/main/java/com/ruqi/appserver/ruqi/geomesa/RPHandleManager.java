package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.GeoRecommendRelatedId;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.*;

/**
 * 推荐上车点的数据处理管理类
 */
public class RPHandleManager {
    private static RPHandleManager ins;
    private DataStore  mRecordDataStore;//记录表的的store
    private DataStore  mRecommendPointDataStore;//推荐点的store
    private DataStore  mPointRelatedDataStore;//关联点的store

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

    public void saveRecommendRecord(UploadRecommendPointRequest<RecommendPoint> record){
        ArrayList<UploadRecommendPointRequest<RecommendPoint>> records=new ArrayList<>();
        records.add(record);
        saveRecommendRecords(records);
    }
    public void saveRecommendRecords(List<UploadRecommendPointRequest<RecommendPoint>> records){
        List<String> recordIds = saveRecommendPointsRecords(records);//存储记录总表
        saveRecommendPoints(records,recordIds);//推荐点记录表

    }


    private void saveRecommendPointsRelated(List<GeoRecommendRelatedId> datas) {
        if (mPointRelatedDataStore == null) {
            mPointRelatedDataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_AND_RECOMMEND_RELATED);
        }
        SimpleFeatureType sft=GeoTable.getRecommendRelatedSimpleType();
        GeoDbHandler.createSchema(mPointRelatedDataStore, sft);
        List<SimpleFeature> relatedDatas=convertRecordsToRelatedSF(datas,sft);
        try {
            GeoDbHandler.writeFeaturesData(mPointRelatedDataStore,sft,relatedDatas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRecommendPoints(List<UploadRecommendPointRequest<RecommendPoint>> records,List<String> recordIds) {
        if (mRecommendPointDataStore == null) {
            mRecommendPointDataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMOND_PONIT);
        }
        SimpleFeatureType sft=GeoTable.getRecommendPointSimpleType();
        GeoDbHandler.createSchema(mRecommendPointDataStore, sft);
        List<SimpleFeature> pointsDatas=convertRecommendPointToSF(records,sft,recordIds);
        storeInDb(mRecommendPointDataStore,pointsDatas,sft);
    }

    private List<SimpleFeature> convertRecommendPointToSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft,List<String> recordIds) {
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
                    }
                }
            }
            saveRecommendPointsRelated(relatedIds);
        }
        return datas;
    }

    /**
     * 存储推荐点列表记录列表
     * @param records
     */
    private List<String> saveRecommendPointsRecords(List<UploadRecommendPointRequest<RecommendPoint>> records){
        if (mRecordDataStore == null) {
            mRecordDataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_PONIT_RECORD);
        }
        SimpleFeatureType sft=GeoTable.getRecommendRecordSimpleType();
        List<SimpleFeature> recordsDatas=convertRecordToPointSF(records,sft);
        List<String>  recordIds=getRecordIDs(recordsDatas);
        storeInDb(mRecordDataStore,recordsDatas,sft);
        return recordIds;
    }

    private List<SimpleFeature> convertRecordToPointSF(List<UploadRecommendPointRequest<RecommendPoint>> records, SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        if (records!=null&&!records.isEmpty()){
            int recommendRecordSize=records.size();
            for (int i = 0; i < recommendRecordSize ; i++) {
                datas.add(GeoMesaDataWrapper.convertRecordToRecordSF(records.get(i),sft));
            }
        }
        return datas;
    }

    private void storeInDb(DataStore mDataStore, List<SimpleFeature> recordsDatas, SimpleFeatureType sft) {
        GeoDbHandler.createSchema(mDataStore, sft);
        try {
            GeoDbHandler.writeFeaturesData(mDataStore,sft,recordsDatas);
        } catch (IOException e) {
            e.printStackTrace();
        }
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



}
