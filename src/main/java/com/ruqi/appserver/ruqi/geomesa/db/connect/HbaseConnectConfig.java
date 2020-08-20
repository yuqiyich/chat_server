package com.ruqi.appserver.ruqi.geomesa.db.connect;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * hbase连接参数
 */
@Component
public class HbaseConnectConfig {
    /****************相关配置的key值 start************************/
    //写入zookeeper的配置，连接hbase集群
    public static String KEY_ZOOKEEPER = "hbase.zookeepers";
    public static String KEY_CATALOG = "hbase.catalog";
    /****************相关配置的key值 end************************/
    /****************相关配置的默认值 start************************/
    //zookeeper的的连接集群配置
    public static String VALUE_ZOOKEEPER="hdpcore01:2181,hdpcore02:2181,hdpcore03:2181";//默认为自己机器ip,这样可以跑main函数

    @Value("${zookeeper.hosts}")
    public void setPort(String port) {
        HbaseConnectConfig.VALUE_ZOOKEEPER = port;
    }
}
