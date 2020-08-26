package com.ruqi.appserver.ruqi.geomesa.db.connect;

import com.mysql.cj.util.LRUCache;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import java.io.IOException;
import java.util.HashMap;

/**
 * hbase+geomesa的数据连接管理者
 */
public class MesaDataConnectManager implements IDataConnectManager {
    public static final int MAX_DATA_CONNECT=100;//连接表的最大数据
    private static MesaDataConnectManager ins;


    public static MesaDataConnectManager getIns(){
        synchronized (RPHandleManager.class){
            if (ins==null){
                ins=new MesaDataConnectManager();
            }
        }
        return ins;
    }
    private MesaDataConnectManager(){
    }

    private LRUCache<String,DataStore> mCaches=new LRUCache<String, DataStore>(MAX_DATA_CONNECT);


    @Override
    public DataStore getDataStore(String tableName) {
        DataStore cache = mCaches.get(tableName);
        if (cache == null) {
            try {
            HashMap<String, String> configs = new HashMap<>();
            configs.put(HbaseConnectConfig.KEY_ZOOKEEPER, HbaseConnectConfig.VALUE_ZOOKEEPER);
            configs.put(HbaseConnectConfig.KEY_CATALOG, tableName);
            cache = DataStoreFinder.getDataStore(configs);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCaches.put(tableName, cache);
        }
        return cache;
    }

    @Override
    public void disposeDataStore(String tableName) {
              DataStore cache = mCaches.get(tableName);
             if (cache!=null){
                 cache.dispose();
             }
    }
}
