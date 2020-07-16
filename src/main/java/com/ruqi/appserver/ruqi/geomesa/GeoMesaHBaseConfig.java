package com.ruqi.appserver.ruqi.geomesa;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeoMesaHBaseConfig {
    public static final String HBASE_CATALOG = "hbase.catalog";
    public static final String HBASE_ZOOKEEPERS = "hbase.zookeepers";

    public static final String HBASE_TYPE_NAME = "demo";

    // 本地运行测试时使用localhost，使用外部集群时使用"127.0.0.1:2181"的形式
    public static final String HBASE_ZOOKEEPERS_VALUE = "localhost:2181";

    public static final String HBASE_DEMO_TYPE_VALUE = "test-table";

    public static Map getDataStoreParams(String catalog, String zookeepers) {
        Map params = new HashMap();
        params.put(HBASE_CATALOG, catalog);
        params.put(HBASE_ZOOKEEPERS, zookeepers);
        return params;
    }

    public static DataStore getTestDemoHBaseDataStore() throws IOException {
        return getHBaseDataStore(HBASE_TYPE_NAME, HBASE_ZOOKEEPERS_VALUE);
    }

    public static DataStore getHBaseDataStore(String catalog, String zookeepers) throws IOException {
        return DataStoreFinder.getDataStore(getDataStoreParams(catalog, zookeepers));
    }
}
