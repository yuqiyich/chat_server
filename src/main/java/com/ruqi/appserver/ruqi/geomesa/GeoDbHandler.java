package com.ruqi.appserver.ruqi.geomesa;

import org.geotools.data.*;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.factory.Hints;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.sort.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GeoDbHandler {
    private static Logger logger = LoggerFactory.getLogger(GeoDbHandler.class);
    /**
     * 获取某表的连接
     *
     * @param tableName
     * @return
     */
    public static   DataStore getHbaseTableDataStore(String tableName) {
        DataStore dataStore = null;
        try {
            HashMap<String,String> configs=new HashMap<>();
            configs.put(HbaseConnectConfig.KEY_ZOOKEEPER,HbaseConnectConfig.VALUE_ZOOKEEPER);
            configs.put(HbaseConnectConfig.KEY_CATALOG,tableName);
            dataStore = DataStoreFinder.getDataStore(configs);
            return dataStore;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     *
     * @param datastore
     * @param sft
     * @param features 地理数据的类型
     * @throws IOException
     */
    public static void writeFeaturesData(DataStore datastore, SimpleFeatureType sft, List<SimpleFeature> features) throws IOException {
        if (features.size() > 0) {
            // use try-with-resources to ensure the writer is closed
            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                         datastore.getFeatureWriterAppend(sft.getTypeName(), Transaction.AUTO_COMMIT)) {
                for (SimpleFeature feature : features) {
                    // using a geotools writer, you have to get a feature, modify it, then commit it
                    // appending writers will always return 'false' for haveNext, so we don't need to bother checking
//                    logger.info("Writing test data start");
                    SimpleFeature toWrite = writer.next();

                    // copy attributes
                    toWrite.setAttributes(feature.getAttributes());

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
            logger.info("Wrote " + features.size() + " features");
            logger.info("");
        }
    }

    public static void createSchema(DataStore dataStore, SimpleFeatureType sft) {
        logger.info("Creating schema: " + DataUtilities.encodeType(sft));
        // we only need to do the once - however, calling it repeatedly is a no-op
        try {
            dataStore.createSchema(sft);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("");
    }

    public static  void queryFeature(DataStore datastore, List<Query> queries) throws IOException {
        for (Query query : queries) {
            logger.info("Running query " + ECQL.toCQL(query.getFilter()));
            if (query.getPropertyNames() != null) {
                logger.info("Returning attributes " + Arrays.asList(query.getPropertyNames()));
            }
            if (query.getSortBy() != null) {
                SortBy sort = query.getSortBy()[0];
                logger.info("Sorting by " + sort.getPropertyName() + " " + sort.getSortOrder());
            }
            // submit the query, and get back an iterator over matching features
            // use try-with-resources to ensure the reader is closed
            try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                         datastore.getFeatureReader(query, Transaction.AUTO_COMMIT)) {
                // loop through all results, only print out the first 10
                int n = 0;
                while (reader.hasNext()) {
                    SimpleFeature feature = reader.next();
                    if (n++ < 10) {
                        // use geotools data utilities to get a printable string
                        logger.info(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                    } else if (n == 10) {
                        logger.info("...");
                    }
                }
                logger.info("Returned " + n + " total features");
                logger.info("");
            }
        }
    }
}
