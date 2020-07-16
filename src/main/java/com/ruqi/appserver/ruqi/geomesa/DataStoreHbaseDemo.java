package com.ruqi.appserver.ruqi.geomesa;

import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.io.IOException;
import java.util.*;

public class DataStoreHbaseDemo {
    private List<Query> queries;

    public static void main(String[] args) {
        DataStoreHbaseDemo dataStoreHbaseDemo = new DataStoreHbaseDemo();
        dataStoreHbaseDemo.printfHbase();
    }

    public void printfHbase() {
        try {
            DataStore dataStore = GeoMesaHBaseConfig.getTestDemoHBaseDataStore();
            if (dataStore != null) {
                //获取Catalog下所有的数据表
                String[] typeNames = dataStore.getTypeNames();
                if (typeNames.length > 0) {
                    for (int index = 0; index < typeNames.length; index++) {
                        System.out.println("\n--->typeNames[" + index + "]:" + typeNames[index]);
                        //获取第一张数据表的数据信息
                        SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeNames[index]);
                        if (featureSource != null) {
                            //获取该数据表的属性信息
                            SimpleFeatureType featureType = featureSource.getSchema();
                            System.out.println("--->表名:" + featureType.getTypeName());
                            System.out.println("--->字段数:" + featureType.getAttributeCount());
                            //获取数据表的属性(字段)结构
                            List<AttributeDescriptor> attributeDescriptors = featureType.getAttributeDescriptors();
                            for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
                                System.out.println("--->字段名:" + attributeDescriptor.getLocalName());
                            }
                            //获取数据表的记录信息
                            SimpleFeatureCollection features = featureSource.getFeatures();
                            SimpleFeatureIterator featureIterator = features.features();
                            //打印前10条记录
                            int counter = 0;
                            while (featureIterator.hasNext()) {
                                System.out.println("\n--->数据项：");
                                //一条记录集
                                SimpleFeature simpleFeature = featureIterator.next();
                                //获取记录集信息
                                Collection<Property> properties = simpleFeature.getProperties();
                                Iterator<Property> iterator = properties.iterator();
                                while (iterator.hasNext()) {
                                    Property property = iterator.next();
                                    System.out.println(property.getName().getLocalPart() + ":" + property.getValue().toString());
                                }
                                if (counter > 10) {
                                    break;
                                }
                                counter++;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Query> getTestQueries() {
        if (queries == null) {
            try {
                List<Query> queries = new ArrayList<>();

                String during = "dtg DURING 2159-01-01T00:00:00.000Z/2169-01-01T00:00:00.000Z";
                String bbox = "bbox(geom,128,48,133,53)";

                //时空查询
                queries.add(new Query(GeoMesaHBaseConfig.HBASE_TYPE_NAME, ECQL.toFilter(bbox + " AND " + during)));
                //空间查询
                queries.add(new Query(GeoMesaHBaseConfig.HBASE_TYPE_NAME, ECQL.toFilter(bbox)));
                //时间查询
                queries.add(new Query(GeoMesaHBaseConfig.HBASE_TYPE_NAME, ECQL.toFilter(during)));
                // basic spatio-temporal query with projection down to a few attributes
                this.queries = Collections.unmodifiableList(queries);
            } catch (CQLException e) {
                throw new RuntimeException("Error creating filter:", e);
            }
        }
        return queries;
    }
}
