package com.ruqi.appserver.ruqi.geomesa.db;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruqi.appserver.ruqi.geomesa.db.connect.HbaseConnectConfig;
import com.ruqi.appserver.ruqi.geomesa.db.connect.MesaDataConnectManager;
import org.apache.commons.lang.StringUtils;

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

public class GeoDbHandler {
    private static Logger logger = LoggerFactory.getLogger(GeoDbHandler.class);
    private static boolean IS_DB_DEBUG=false;

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

    public  static void setDebug(boolean isDebug){
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
            for (SimpleFeature feature : features) {
                try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                             datastore.getFeatureWriter(sft.getTypeName(), ECQL.toFilter(fiDName + "='" + feature.getID() + "'"), Transaction.AUTO_COMMIT)) {
                    logger.info(" [" + fiDName + "=  " + feature.getID() );
                    boolean hasOldData = false;
                    while (writer.hasNext()) {
                        SimpleFeature next = writer.next();
                        if (next != null) {
                            hasOldData = true;
                            if (iUpdateDataListener != null) {
                                iUpdateDataListener.updateData(next, feature);
                                writer.write();
                            } else {
                                //or throw error ?
                                logger.error("no iUpdateData listener，then return");
                                return;
                            }
                        }
//                     next.setAttribute("cityName", "武汉是");
//                     writer.write(); // or, to delete it: writer.remove();
                    }
                    if (!hasOldData) {
                        newData.add(feature);
                    }
                } catch (IOException | CQLException e) {
                    e.printStackTrace();
                }
            }
            if (newData.size() > 0) {
                try {
                    //FIXME 如果新增数据中有fid一样的数据，怎么处理
                    logger.info("there are new data,then write new " + newData.size() + " datas");
                    writeNewFeaturesData(datastore, sft, newData);
                } catch (IOException e) {
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
                    logger.debug("start  write  features for type:" + sft.getTypeName()+";"+DataUtilities.encodeFeature(feature));
                    // using a geotools writer, you have to get a feature, modify it, then commit it
                    // appending writers will always return 'false' for haveNext, so we don't need to bother checking
//                    logger.info("Writing test data start");
                    SimpleFeature toWrite = writer.next();


                    Iterator<Property> properties= feature.getProperties().iterator();
                    while(properties.hasNext()){
                        Property property=properties.next();
                        toWrite.setAttribute(property.getName(),feature.getAttribute(property.getName()));
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

                    // alternatively, you can use the PROVIDED_FID hint directly
                    // toWrite.getUserData().put(Hints.PROVIDED_FID, feature.getID());

                    // if no feature ID is set, a UUID will be generated for you

                    // make sure to copy the user data, if there is any
                    toWrite.getUserData().putAll(feature.getUserData());

                    // write the feature
                    writer.write();
                }
            }
            logger.info("Wrote " + features.size() + " features for " + sft.getTypeName());
        }
    }

    public static void createOrUpdateSchema(DataStore dataStore, SimpleFeatureType sft) {
        logger.info("Creating new  schema: " + DataUtilities.encodeType(sft));
        // we only need to do the once - however, calling it repeatedly is a no-op
        try {
            SimpleFeatureType oldSft = dataStore.getSchema(sft.getTypeName());
            if (oldSft!=null && sft.getAttributeCount()!=oldSft.getAttributeCount()){
                logger.error("there is old  schema : " + DataUtilities.encodeType(oldSft)+"；update old to new schema And new add schema:");
                //todo how use java api to update schema
//                dataStore.updateSchema(sft.getTypeName(),GeoTable.getAddAttrSFT(sft.getTypeName()));
                throw new UnsupportedOperationException(sft.getTypeName()+" can not update schema by java api ");
            }else {
                dataStore.createSchema(sft);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("");
    }

    public static List<SimpleFeature> queryFeature(DataStore datastore, List<Query> queries) throws IOException {
        for (Query query : queries) {
            logger.info("["+((HBaseDataStore) datastore).config().catalog()+"] table Running query " + ECQL.toCQL(query.getFilter())+"；typeName:"+query.getTypeName());
            if (IS_DB_DEBUG){
                if (query.getPropertyNames() != null) {
                    logger.info("Returning attributes " + Arrays.asList(query.getPropertyNames()));
                }
                if (query.getSortBy() != null) {
                    SortBy sort = query.getSortBy()[0];
                    logger.info("Sorting by " + sort.getPropertyName() + " " + sort.getSortOrder());
                }
            }
            List<SimpleFeature> sfs=new ArrayList<>();
            // submit the query, and get back an iterator over matching features
            // use try-with-resources to ensure the reader is closed
            try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                         datastore.getFeatureReader(query, Transaction.AUTO_COMMIT)) {
                // loop through all results, only print out the first 10
                int n = 0;
                while (reader.hasNext()) {
                    SimpleFeature feature = reader.next();
                    if (IS_DB_DEBUG){
                        if (n++ < 100) {
                            // use geotools data utilities to get a printable string
                            logger.info(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                        } else if (n == 100) {
                            logger.info("...");
                        }
                    }
                    sfs.add(feature);
                }
                if (IS_DB_DEBUG) {
                    logger.info("Returned " + n + " total features");

                }
            }catch (IOException e){
                logger.error("query error",e);
            }
            logger.info("Returned--result size:"+sfs.size());
            return  sfs;
        }
        return null;
    }

    /**
     * 查询表的行数
     *
     * @param tableName 表名
     * @param cqlFilter  可为空,统计查询的限制条件
     * @return
     * @throws IOException
     */
    public static int queryTableRowCount(String tableName,String cqlFilter)  {
        if (!StringUtils.isEmpty(tableName)) {
            int count=0;
            try {
            DataStore datastore=MesaDataConnectManager.getIns().getDataStore(tableName);
            String[] typeNames= datastore.getTypeNames();
            Query query = new Query(typeNames[0]);
            query.getHints().put(QueryHints.STATS_STRING(), "Count()");
             try {
                 if (!StringUtils.isEmpty(cqlFilter)){
                     logger.info("count table"+tableName+"with filter:"+cqlFilter);
                     query.setFilter(ECQL.toFilter(cqlFilter));
                 }
            } catch (CQLException e) {
                e.printStackTrace();
            }
             List<SimpleFeature> queryResults = queryFeature(datastore,Arrays.asList(query));
              if (queryResults!=null&&queryResults.size()==1){
                  SimpleFeature feature = queryResults.get(0);
                  if ("stat".equals(feature.getID())) {
                      for (Property property : feature.getValue()
                      ) {
                          if ("stats".equals(property.getName().getURI())) {
                              JsonElement jsonObject=JsonParser.parseString((String)property.getValue());
                              logger.info("...property count:" + property.getValue());
                               count = ((JsonObject) jsonObject).get("count").getAsInt();
                          }
                      }
                  }
              }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return count;
        }
        //some error
        return -1;
    }



}
