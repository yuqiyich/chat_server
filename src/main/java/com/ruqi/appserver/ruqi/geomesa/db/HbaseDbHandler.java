package com.ruqi.appserver.ruqi.geomesa.db;

import com.ruqi.appserver.ruqi.geomesa.db.connect.HbaseConnectConfig;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HbaseDbHandler {
    private static Connection connection;
    private static Admin admin;
    private static void init(){
        if (admin==null){
            Configuration configuration= HBaseConfiguration.create();
            configuration.set(HbaseConnectConfig.KEY_HBASE_ZOOKEEPER,HbaseConnectConfig.VALUE_ZOOKEEPER);
            try{
                connection = ConnectionFactory.createConnection(configuration);
                admin = connection.getAdmin();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static List<String> getGeoTableNames(String tableNamePrifix) {
        if (admin==null){
            init();
        }
        HTableDescriptor hTableDescriptors[];
        List<String> names = new ArrayList<>();
        try {
            hTableDescriptors = admin.listTables();
            for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
                String name = hTableDescriptor.getNameAsString();
//                System.out.println("table name:" + name);
                if (name.startsWith(tableNamePrifix)&&countString(name,"_")==3) {
                    names.add(name);
//                    System.out.println("========bingo==============table name:" + name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    public static int countString(String str,String s) {
        int count = 0;
        while (str.contains(s)) {
            str = str.substring(str.indexOf(s) + 1, str.length());
            count++;
        }
        return count;
    }

    private static void close() {
        try {
            if (admin != null) {
                admin.close();
            }
            if (null != connection) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
