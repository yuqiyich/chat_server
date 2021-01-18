package com.ruqi.appserver.ruqi.geomesa.db;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruqi.appserver.ruqi.geomesa.db.connect.MesaDataConnectManager;
import com.ruqi.appserver.ruqi.geomesa.db.updateListener.RecommendRecordDataUpdater;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.common.value.qual.StringVal;
import org.geotools.data.*;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.factory.Hints;
import org.locationtech.geomesa.hbase.data.HBaseDataStore;
import org.locationtech.geomesa.index.conf.QueryHints;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.sort.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.*;

public class GeoDbHandler {
    private static Logger logger = LoggerFactory.getLogger(GeoDbHandler.class);
    private static boolean IS_DB_DEBUG = false;

    /**
     * 更新表数据的监听器 ，这边默认是根据fid来判断数据的
     */
    public interface IUpdateDataListener {
        /**
         * @param oldData 要更新的老数据
         * @param newData 准备的新数据
         */
        void updateData(SimpleFeature oldData, SimpleFeature newData);
    }

    public static void setDebug(boolean isDebug) {
        IS_DB_DEBUG = isDebug;
    }

    /**
     * 获取某表的连接
     *
     * @param tableName
     * @return
     */
    public static DataStore getHbaseTableDataStore(String tableName) {
        try {
            return MesaDataConnectManager.getIns().getDataStore(tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新有标识的fid数据（如果数据fid不存在，则插入数据）
     *
     * @param datastore
     * @param sft
     * @param features
     * @param fiDName
     */
    public static void updateExistDataOrInsert(DataStore datastore, SimpleFeatureType sft, List<SimpleFeature> features, String fiDName, IUpdateDataListener iUpdateDataListener) {
        if (!StringUtils.isEmpty(fiDName)) {
            String[] attrNames = DataUtilities.attributeNames(sft);
            List<String> attrNamesist = new ArrayList<String>(Arrays.asList(attrNames));
            if (!attrNamesist.contains(fiDName)) {
                logger.error(" [" + fiDName + "] dont exits in  " + attrNamesist.toString());
            }
            List<SimpleFeature> newData = new ArrayList<>();
            //ID IN ('river.1', 'river.2') INFO 这里一定要写好CQL语句要不然会很慢，之前用的每一个ID查一下，就会超级慢
            Map<String, SimpleFeature> tempDatas = new HashMap<>();
            StringBuilder cql = new StringBuilder(fiDName + " IN (");
            for (SimpleFeature feature : features) {
                tempDatas.put(feature.getID(), feature);
                cql.append("'" + feature.getID() + "'" + ",");
            }
            cql.deleteCharAt(cql.length() - 1);
            cql.append(")");
            logger.info("check fid in db cql:"+cql.toString());
            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                         datastore.getFeatureWriter(sft.getTypeName(), ECQL.toFilter(cql.toString()), Transaction.AUTO_COMMIT)) {
                while (writer.hasNext()) {
                    SimpleFeature next = writer.next();
                    if (next != null) {
                        if (iUpdateDataListener != null) {
                            SimpleFeature oldSf = tempDatas.get(next.getID());
                            tempDatas.remove(next.getID());//移除旧的key的数据
                            if (oldSf != null) {
                                logger.debug("update old data id:"+next.getID());
                                iUpdateDataListener.updateData(next, oldSf);
                                writer.write();
                            } else {
                                if (IS_DB_DEBUG) {
                                    logger.error("id:" + next.getID() + "can not find in new Feature");
                                }
                                //nothing contiue next one
                            }

                        } else {
                            //or throw error ?
                            logger.error("no iUpdateData listener，then return");
                            return;
                        }
                    }
//                     next.setAttribute("cityName", "武汉是");
//                     writer.write(); // or, to delete it: writer.remove();
                }
                //添加新的数据到新的列表中
                tempDatas.forEach((k, v) -> newData.add(v));
            } catch (IOException | CQLException e) {
                e.printStackTrace();
            }
            logger.info("updateExistDataOrInsert--> update " + features.size() + " features for " + sft.getTypeName()+"newData:"+newData.size());
            if (newData.size() > 0) {
                try {
                    //FIXME 如果新增数据中有fid一样的数据，怎么处理
                    logger.info("there are new data,then write new " + newData.size() + " datas");
                    writeNewFeaturesData(datastore, sft, newData);
                } catch (IOException e) {
                    logger.error("write new data error", e);
                    e.printStackTrace();
                }
            }
        } else {
            logger.error("no fid meta data ");
        }
    }

    /**
     * 存储新的数据
     *
     * @param datastore
     * @param sft
     * @param features  地理数据的类型
     * @throws IOException
     */
    public static void writeNewFeaturesData(DataStore datastore, SimpleFeatureType sft, List<SimpleFeature> features) throws IOException {
        if (features.size() > 0) {
            //FIXME 如果新增数据中有fid一样的数据，怎么处理
            // use try-with-resources to ensure the writer is closed
            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                         datastore.getFeatureWriterAppend(sft.getTypeName(), Transaction.AUTO_COMMIT)) {
                for (SimpleFeature feature : features) {
                    logger.info("start  write  features for type:" + sft.getTypeName() + ";" + DataUtilities.encodeFeature(feature));
                    // using a geotools writer, you have to get a feature, modify it, then commit it
                    // appending writers will always return 'false' for haveNext, so we don't need to bother checking
//                    logger.info("Writing test data start");
                    SimpleFeature toWrite = writer.next();


                    Iterator<Property> properties = feature.getProperties().iterator();
                    while (properties.hasNext()) {
                        Property property = properties.next();
                        toWrite.setAttribute(property.getName(), feature.getAttribute(property.getName()));
                    }
                    // copy attributes
//
//                    toWrite.setAttributes(feature.getAttributes());
//                    int attrSize=sft.getAttributeCount();
//                    for (int i = 0; i < attrSize; i++) {
//                        toWrite.setAttribute(sft.getType(i).getName(),feature.getAttribute(i));
//                    }

                    // if you want to set the feature ID, you have to cast to an implementation class
                    // and add the USE_PROVIDED_FID hint to the user data
                    ((FeatureIdImpl) toWrite.getIdentifier()).setID(feature.getID());
                    toWrite.getUserData().put(Hints.USE_PROVIDED_FID, Boolean.TRUE);
                    if (feature.getID().equals("113.33599_23.1074")){
                        logger.error("xxxx113.33599_23.1074xxxx can not be create ,becasue it is in Db");
                    }
                    // alternatively, you can use the PROVIDED_FID hint directly
                    // toWrite.getUserData().put(Hints.PROVIDED_FID, feature.getID());

                    // if no feature ID is set, a UUID will be generated for you

                    // make sure to copy the user data, if there is any
                    toWrite.getUserData().putAll(feature.getUserData());

                    // write the feature
                    writer.write();

                }
            }
            logger.info("writeNewFeaturesData-->Wrote " + features.size() + " features for " + sft.getTypeName());
        }
    }

    public static void createOrUpdateSchema(DataStore dataStore, SimpleFeatureType sft) {
//        logger.info("Creating new  schema: " + DataUtilities.encodeType(sft));
        // we only need to do the once - however, calling it repeatedly is a no-op
        try {
            SimpleFeatureType oldSft = dataStore.getSchema(sft.getTypeName());
            if (oldSft != null && sft.getAttributeCount() != oldSft.getAttributeCount()) {
                logger.error("there is old  schema : " + DataUtilities.encodeType(oldSft) + "；update old to new schema And new add schema:");
                //todo how use java api to update schema
//                dataStore.updateSchema(sft.getTypeName(),GeoTable.getAddAttrSFT(sft.getTypeName()));
                throw new UnsupportedOperationException(sft.getTypeName() + " can not update schema by java api ");
            } else {
                dataStore.createSchema(sft);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
//        logger.info("");
    }

    public static List<SimpleFeature> queryFeature(DataStore datastore, List<Query> queries) throws IOException {
        for (Query query : queries) {
            logger.info("[" + ((HBaseDataStore) datastore).config().catalog() + "] table Running query " + ECQL.toCQL(query.getFilter()) + "；typeName:" + query.getTypeName());
            if (IS_DB_DEBUG) {
                if (query.getPropertyNames() != null) {
                    logger.info("Returning attributes " + Arrays.asList(query.getPropertyNames()));
                }
                if (query.getSortBy() != null) {
                    SortBy sort = query.getSortBy()[0];
                    logger.info("Sorting by " + sort.getPropertyName() + " " + sort.getSortOrder());
                }
            }
            List<SimpleFeature> sfs = new ArrayList<>();
            // submit the query, and get back an iterator over matching features
            // use try-with-resources to ensure the reader is closed
            try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                         datastore.getFeatureReader(query, Transaction.AUTO_COMMIT)) {
                // loop through all results, only print out the first 10
                int n = 0;
                long time1 = System.currentTimeMillis();
                while (reader.hasNext()) {
                    long time2 = System.currentTimeMillis();
                    if (IS_DB_DEBUG) {
                        logger.info("--->ttt1: " + (time2 - time1) + ", index=" + n);
                    }
                    SimpleFeature feature = reader.next();
                    time1 = System.currentTimeMillis();
                    if (IS_DB_DEBUG) {
                        logger.info("--->ttt2: " + (time1 - time2) + ", index=" + n);
                    }

                    sfs.add(feature);

                    int printLimit = 100;
                    if (IS_DB_DEBUG && n < printLimit) {
                        n++;
                        if (n < printLimit) {
                            // use geotools data utilities to get a printable string
                            logger.info(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                        } else if (n == printLimit) {
                            logger.info("...");
                        }
                    }
                }
                if (IS_DB_DEBUG) {
                    logger.info("Returned " + n + " total features");
                }
            } catch (IOException e) {
                logger.error("query error", e);
            }
            logger.info("Returned--result size:" + sfs.size());
            return sfs;
        }
        return null;
    }

    /**
     * 查询表的行数
     *
     * @param tableName 表名
     * @param cqlFilter 可为空,统计查询的限制条件
     * @return
     * @throws IOException
     */
    public static int queryTableRowCount(String tableName, String cqlFilter) {
        if (!StringUtils.isEmpty(tableName)) {
            int count = 0;
            try {
                DataStore datastore = MesaDataConnectManager.getIns().getDataStore(tableName);
                if (datastore != null) {
                    String typeName = MesaDataConnectManager.getIns().getTableTypeName(tableName);
                    Query query = new Query(typeName);
                    query.getHints().put(QueryHints.STATS_STRING(), "Count()");
                    try {
                        if (!StringUtils.isEmpty(cqlFilter)) {
                            logger.info("count table" + tableName + "with filter:" + cqlFilter);
                            query.setFilter(ECQL.toFilter(cqlFilter));
                        }
                    } catch (CQLException e) {
                        e.printStackTrace();
                    }
                    List<SimpleFeature> queryResults = queryFeature(datastore, Arrays.asList(query));
                    if (queryResults != null && queryResults.size() == 1) {
                        SimpleFeature feature = queryResults.get(0);
                        if ("stat".equals(feature.getID())) {
                            for (Property property : feature.getValue()
                            ) {
                                if ("stats".equals(property.getName().getURI())) {
                                    JsonElement jsonObject = JsonParser.parseString((String) property.getValue());
                                    logger.info("...property count:" + property.getValue());
                                    count = ((JsonObject) jsonObject).get("count").getAsInt();
                                }
                            }
                        }
                    }
                } else {
                    logger.error("[" + tableName + "] can not get dataAccess ,and Filter is:" + cqlFilter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }
        //some error
        return -1;
    }

    // 查询表内的group by 的Key的集合
    public static List<String> queryGroupKeys(String tableName, String cqlFilter, String groupKey, int minSize) {
        List<String> keyList = new ArrayList<>();
        if (!StringUtils.isEmpty(tableName)) {
            try {
                DataStore datastore = MesaDataConnectManager.getIns().getDataStore(tableName);
                if (datastore != null) {
                    Query query = new Query(MesaDataConnectManager.getIns().getTableTypeName(tableName));
//                    query.getHints().put(QueryHints.STATS_STRING(), "Enumeration(\"" + groupKey + "\")");
                    query.getHints().put(QueryHints.STATS_STRING(), "GroupBy(\"" + groupKey + "\",Count())");
                    try {
                        if (!StringUtils.isEmpty(cqlFilter)) {
                            logger.info("count table " + tableName + " with filter:" + cqlFilter);
                            query.setFilter(ECQL.toFilter(cqlFilter));
                        }
                    } catch (CQLException e) {
                        e.printStackTrace();
                    }
                    logger.info("--->query:" + query);
                    List<SimpleFeature> queryResults = queryFeature(datastore, Arrays.asList(query));
//                    logger.info("--->queryResults:" + queryResults);
                    if (queryResults != null && queryResults.size() > 0) {
                        SimpleFeature feature = queryResults.get(0);
                        if ("stat".equals(feature.getID())) {
                            for (Property property : feature.getValue()) {
                                if ("stats".equals(property.getName().getURI())) {
                                    JsonObject jsonObject = (JsonObject) JsonParser.parseString((String) property.getValue());
                                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                                        String groupKeyValue = entry.getKey();
                                        int count = ((JsonObject) entry.getValue()).get("count").getAsInt();
                                        if (count < minSize || (groupKeyValue.endsWith("00") && groupKey == GeoTable.KEY_AD_CODE)) {
                                            continue;
                                        }
                                        keyList.add(groupKeyValue);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    logger.error("[" + tableName + "] can not get dataAccess ,and Filter is:" + cqlFilter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return keyList;
    }

    // 查询表内的group by 的统计总量
    public static Map<String, Integer> queryGroupCount(String tableName, String cqlFilter, @StringVal({GeoTable.KEY_AD_CODE, KEY_CITY_CODE}) String groupKey) {
        Map<String, Integer> countMap = new HashMap<>();
        if (!StringUtils.isEmpty(tableName)) {
            try {
                DataStore datastore = MesaDataConnectManager.getIns().getDataStore(tableName);
                if (datastore != null) {
                    String typeName = MesaDataConnectManager.getIns().getTableTypeName(tableName);
                    Query query = new Query(typeName);
                    query.getHints().put(QueryHints.STATS_STRING(), "GroupBy(\"" + groupKey + "\",Count())");
                    logger.info("--->query:" + query);
                    logger.info("--->query.getHints():" + query.getHints());
                    try {
                        if (!StringUtils.isEmpty(cqlFilter)) {
                            logger.info("count table " + tableName + " with filter:" + cqlFilter);
                            query.setFilter(ECQL.toFilter(cqlFilter));
                        }
                    } catch (CQLException e) {
                        e.printStackTrace();
                    }
                    long time1 = System.currentTimeMillis();
                    List<SimpleFeature> queryResults = queryFeature(datastore, Arrays.asList(query));
                    long time2 = System.currentTimeMillis();
                    logger.info("--->queryFeature time=" + (time2 - time1) / 1000);
                    if (queryResults != null && queryResults.size() > 0) {
//                        logger.info("--->queryGroupCount queryResults.size():" + queryResults.size());
//                        for (SimpleFeature feature : queryResults) {
//                            logger.info("--->queryGroupCount feature:" + feature);
//                        }

                        SimpleFeature feature = queryResults.get(0);
                        if ("stat".equals(feature.getID())) {
                            for (Property property : feature.getValue()) {
                                if ("stats".equals(property.getName().getURI())) {
//                                    logger.info("--->queryGroupCount property.getValue():" + property.getValue());
                                    JsonObject jsonObject = (JsonObject) JsonParser.parseString((String) property.getValue());
//                                    {"440100":{"count":400},"440600":{"count":7}}
                                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                                        String groupKeyValue = entry.getKey();
                                        if (null != groupKeyValue && groupKeyValue.length() == 6) {  // code正常是6位数。
                                            if ((groupKey == GeoTable.KEY_AD_CODE && groupKeyValue.endsWith("00"))
                                                    || (groupKey == GeoTable.KEY_CITY_CODE && !groupKeyValue.endsWith("00"))) {
                                                continue;
                                            }
                                            int count = ((JsonObject) entry.getValue()).get("count").getAsInt();
                                            countMap.put(groupKeyValue, count);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    logger.error("[" + tableName + "] can not get dataAccess ,and Filter is:" + cqlFilter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return countMap;
    }

    /**
     * 全球记录表，某个id的数据变更cityCode值
     * updateRecordDataCityCode("pro", "06fd83f4-cbd4-41ed-87e1-a0cb639d80f4", "440100");
     *
     * @param env         环境
     * @param rrid        id值，通过查询得到
     * @param newCityCode 新的cityCode
     */
    public static void updateRecordDataCityCode(String env, String rrid, String newCityCode) {
        SimpleFeatureType sft = GeoTable.getRecommendRecordSimpleType(GeoTable.WORLD_CODE, true);
        List<SimpleFeature> features = new ArrayList<>();
        SimpleFeatureBuilderWrapper builder = new SimpleFeatureBuilderWrapper(sft);
        builder.set(PRIMARY_KEY_TYPE_RECOMMEND_POINT, rrid);
        builder.set(KEY_CITY_CODE, "440100");
        features.add(builder.buildFeature(rrid));
        GeoDbHandler.updateExistDataOrInsert(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX + env + "_" + GeoTable.WORLD_CODE),
                sft, features, PRIMARY_KEY_TYPE_RECOMMEND_RECORD, new RecommendRecordDataUpdater());
    }

}
