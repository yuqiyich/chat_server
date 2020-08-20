//package com.ruqi.appserver.ruqi.es;
//
//import org.elasticsearch.client.Client;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.index.search.SimpleQueryStringQueryParser;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.net.InetAddress;
//
//public class ESUtils {
//    public final static Client client = build();
//    public final static Class clazz = ESUtils.class;
//
//    static Logger logger = LoggerFactory.getLogger(clazz);
//
//    /**
//     * 创建一次
//     *
//     * @return
//     */
//    private static Client build() {
//        if (null != client) {
//            return client;
//        }
//        Client client = null;
//        String ip = "localhost";
//        logger.info(clazz + "获取ESIP地址：" + ip);
//        try {
//            logger.info(clazz + "创建Elasticsearch Client 开始");
//            SimpleQueryStringQueryParser.Settings settings = SimpleQueryStringQueryParser.Settings
//                    .settingsBuilder()
//                    .put("cluster.name", "sojson-application")
//                    .put("client.transport.sniff", true)
//                    .build();
//            client = TransportClient.builder().settings(settings).build()
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), 9300));
//            logger.info(clazz + "创建Elasticsearch Client 结束");
//        } catch (Exception e) {
//            logger.info(clazz + e.getMessage() + "创建Client异常");
//        }
//        return client;
//    }
//
//    /**
//     * 关闭
//     */
//    public static void close() {
//        if (null != client) {
//            try {
//                client.close();
//            } catch (Exception e) {
//
//            }
//        }
//    }
//}
