package com.ruqi.appserver.ruqi.geomesa.db;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * hbase连接参数
 */
@Component
public class HbaseConnectConfig {
    /****************相关配置的key值 start************************/
    //写入zookeeper的配置，连接hbase集群
    static String KEY_ZOOKEEPER = "hbase.zookeepers";
    static String KEY_CATALOG = "hbase.catalog";
    /****************相关配置的key值 end************************/
    @Value("${zookeeper.hosts}")
    private String mConfigzoo="";

    /****************相关配置的默认值 start************************/
    //zookeeper的的连接集群配置
    static String VALUE_ZOOKEEPER="10.10.16.94:2181";//默认为自己机器ip,可以跑main函数

    @Value("${zookeeper.hosts}")
    public void setPort(String port) {
        HbaseConnectConfig.VALUE_ZOOKEEPER = port;
    }
}
