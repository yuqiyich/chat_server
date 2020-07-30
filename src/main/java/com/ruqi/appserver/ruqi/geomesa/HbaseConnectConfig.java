package com.ruqi.appserver.ruqi.geomesa;


/**
 * hbase连接参数
 */
public class HbaseConnectConfig {
    /****************相关配置的key值 start************************/
    //写入zookeeper的配置，连接hbase集群
    static String KEY_ZOOKEEPER = "hbase.zookeepers";
    static String KEY_CATALOG = "hbase.catalog";
    /****************相关配置的key值 end************************/


    /****************相关配置的默认值 start************************/
    //zookeeper的的连接集群配置
    static String VALUE_ZOOKEEPER = "10.10.28.248:2181,10.10.28.247:2181,10.10.28.31:2181";
    /****************相关配置的默认值 end************************/
}
