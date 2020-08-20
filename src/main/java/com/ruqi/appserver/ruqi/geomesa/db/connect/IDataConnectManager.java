package com.ruqi.appserver.ruqi.geomesa.db.connect;

import org.geotools.data.DataStore;

public interface IDataConnectManager {
    /**
     * 根据表名来获取数据表连接
     *
     * @param tableName
     * @return
     */
    DataStore getDataStore(String tableName);

    /**
     * 释放某个表的连接资源
     *
     * @param tableName
     */
    void disposeDataStore(String tableName);
}
