package com.ruqi.appserver.ruqi.geomesa.db.connect;

import com.mysql.cj.util.LRUCache;
import com.mysql.cj.util.StringUtils;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
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
    public static final int MAX_DATA_CONNECT = 100;//连接表的最大数据
    private static MesaDataConnectManager ins;


    public static MesaDataConnectManager getIns() {
        synchronized (RPHandleManager.class) {
            if (ins == null) {
                ins = new MesaDataConnectManager();
            }
        }
        return ins;
    }

    private MesaDataConnectManager() {
    }

    private LRUCache<String, DataStore> mCaches = new LRUCache<String, DataStore>(MAX_DATA_CONNECT);
    private LRUCache<String, String> mTypeNameCaches = new LRUCache<String, String>(MAX_DATA_CONNECT);


    @Override
    public DataStore getDataStore(String tableName) {
//        logger.info(tableName + "get connect start ");
        DataStore cache = mCaches.get(tableName);
        if (cache == null) {
            try {
                logger.info("["+tableName + "]not in memory  ，and create it start ");
                HashMap<String, String> configs = new HashMap<>();
                configs.put(HbaseConnectConfig.KEY_ZOOKEEPER, HbaseConnectConfig.VALUE_ZOOKEEPER);
                configs.put(HbaseConnectConfig.KEY_CATALOG, tableName);
                cache = DataStoreFinder.getDataStore(configs);
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info("["+tableName + "]not in memory  ，and create it ok "+cache);
            mCaches.put(tableName, cache);
        }else {
            logger.info("["+tableName + " ]get cache  end"+cache);
        }
//     logger.info(tableName + " get connect  end");
        return cache;
    }

    public String getTableTypeName(String tableName) {
        String typeName = mTypeNameCaches.get(tableName);
        if (StringUtils.isNullOrEmpty(typeName)) {
            DataStore store = getDataStore(tableName);
            try {
                typeName = store.getTypeNames()[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
            mTypeNameCaches.put(tableName, typeName);
        }
        return typeName;
    }

    @Override
    public void disposeDataStore(String tableName) {
        DataStore cache = mCaches.get(tableName);
        if (cache != null) {
            cache.dispose();
        }
    }
}
