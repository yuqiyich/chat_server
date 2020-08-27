package com.ruqi.appserver.ruqi.geomesa.db.connect;

import com.mysql.cj.util.LRUCache;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * hbase+geomesa的数据连接管理者
 */
public class MesaDataConnectManager implements IDataConnectManager {
    private static Logger logger = LoggerFactory.getLogger(MesaDataConnectManager.class);
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
        logger.debug(tableName+"get connect start ");
        DataStore cache = mCaches.get(tableName);
        if (cache == null) {
            try {
            logger.debug(tableName+"not in memory  ，and create it start ");
            HashMap<String, String> configs = new HashMap<>();
            configs.put(HbaseConnectConfig.KEY_ZOOKEEPER, HbaseConnectConfig.VALUE_ZOOKEEPER);
            configs.put(HbaseConnectConfig.KEY_CATALOG, tableName);
            cache = DataStoreFinder.getDataStore(configs);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.debug(tableName+"not in memory  ，and create it ok ");
            mCaches.put(tableName, cache);
        }
        logger.debug(tableName+" get connect  end");
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
