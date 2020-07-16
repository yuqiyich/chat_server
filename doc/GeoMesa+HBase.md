## 简要介绍
GeoMesa是开源的基于分布式计算系统的面向海量时空数据查询与分析的工具包。
HBase是一个分布式的、面向列的非关系型的开源数据库。

> Tips1：HBase、GeoMesa不配置环境变量理论上不影响。
>
> Tips2：版本、目录等不一致，后续的路径、文件名等可能不一致，请注意区分和替换更改成自己的。
>
> Tips3：查看该说明前最好先查看HBase.md文件了解HBase。
>
 

### 版本说明

GeoMesa：geomesa-hbase_2.11-3.0.0
HBase：hbase-2.2.5

### 简单入门

1. 下载工具包且解压。示例路径为`/Users/zhangyu/geomesa-hbase_2.11-3.0.0`。
2. /Users/zhangyu/geomesa-hbase_2.11-3.0.0/dist/hbase/geomesa-hbase-distributed-runtime-hbase2_2.11-3.0.0.jar 复制到/Users/zhangyu/hbase-2.2.5/lib 目录下。网上部分教程是复制到HBase工程指定的的tmp/hbase/lib目录下，但是2.2.5版本的HBase的tmp/hbase目录下没有lib目录，手动创建复制后也不生效，可能是版本不同则配置方式不一致。
3. /Users/zhangyu/hbase-2.2.5/conf/hbase-site.xml文件内增加配置。
```xml
<property>
	<name>hbase.coprocessor.user.region.classes</name>
	<value>org.locationtech.geomesa.hbase.server.coprocessor.GeoMesaCoprocessor</value>
</property>
```
4. 执行命令启动HBase，查看webUI时，`http://localhost:16030/rs-status`->Software Attributes->Coprocessors的Value为[MultiRowMutationEndpoint]，是默认有的一个协处理器。2、3两个步骤中我们还给HBase加了GeoMesa的协处理器，但是未触发操作前这里还不会显示出来。
5. 执行创建HBase表的操作，再来看4步骤中的协处理器则会显示为[GeoMesaCoprocessor, MultiRowMutationEndpoint]。

### JAVA处理入门
使用到GeoTools相关的工具库，走通这个步骤说明java操作GeoMesa进而操作HBase链路打通了。

GeoMesa数据库结构

|名字| 说明 |
|---|---|
|catalog | 数据库|
| type | 数据表 |
| feature | 数据行 |


1、 pom中配置，确认加载成功。版本与配置需要正确，否则可能出现库的冲突或缺失等问题，这个配置测试通过。

```xml
<repositories>
    <repository>
        <id>osgeo</id>
        <name>OSGeo Release Repository</name>
        <url>https://repo.osgeo.org/repository/release/</url>
        <snapshots><enabled>false</enabled></snapshots>
        <releases><enabled>true</enabled></releases>
    </repository>
    <repository>
        <id>osgeo-snapshot</id>
        <name>OSGeo Snapshot Repository</name>
        <url>https://repo.osgeo.org/repository/snapshot/</url>
        <snapshots><enabled>true</enabled></snapshots>
        <releases><enabled>false</enabled></releases>
    </repository>
</repositories>

<properties>
    <gt.version>23.0</gt.version>
    <hbase.version>2.2.5</hbase.version>
    <geomesa.version>3.0.0</geomesa.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.geotools</groupId>
        <artifactId>gt-main</artifactId>
        <version>${gt.version}</version>
    </dependency>
    <dependency>
        <groupId>org.geotools</groupId>
        <artifactId>gt-cql</artifactId>
        <version>${gt.version}</version>
    </dependency>
    <dependency>
        <groupId>org.geotools</groupId>
        <artifactId>gt-grid</artifactId>
        <version>${gt.version}</version>
    </dependency>
    <dependency>
        <groupId>org.geotools</groupId>
        <artifactId>gt-render</artifactId>
        <version>${gt.version}</version>
    </dependency>
    <dependency>
        <groupId>org.locationtech.geomesa</groupId>
        <artifactId>geomesa-hbase-datastore_2.11</artifactId>
        <version>${geomesa.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.curator</groupId>
        <artifactId>curator-framework</artifactId>
        <version>4.3.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-protocol</artifactId>
        <version>${hbase.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-server</artifactId>
        <version>${hbase.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-common</artifactId>
        <version>${hbase.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-client</artifactId>
        <version>${hbase.version}</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.hbase</groupId>
        <artifactId>hbase-annotations</artifactId>
        <version>${hbase.version}</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

2、 测试创建表与数据。正常情况下代码执行不会有错误且执行完成后能在webUI上看到新增的table。

```java
package com.ruqi.appserver.ruqi.geomesa;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.util.factory.Hints;
import org.locationtech.geomesa.utils.geotools.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IODemo01 {
    /**
     * 这个方法主要设定了表名"index-text02"，
     * 和schema结构"taxiId:String,dtg:Date,*geom:Point:srid=4326"
     *
     * @return SimpleFeatureType，即建表的schema表结构
     */
    public SimpleFeatureType getSimpleFeatureType() {
        SimpleFeatureType sft = SimpleFeatureTypes.createType("index-text02", "taxiId:String,dtg:Date,*geom:Point:srid=4326,description:String");
        //sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, "dtg");
        //设置xz3索引时间间隔为天
        sft.getUserData().put("geomesa.xz3.interval", "day");
        //设置时空索引时间字段为date
        sft.getUserData().put("geomesa.index.dtg", "dateAttr");
        //设置索引精度
        sft.getUserData().put("geomesa.xz.precision", 10);
        return sft;
    }

    public static void main(String[] args) {
        Map<String, String> params = new HashMap<String, String>();
        IODemo01 demo01 = new IODemo01();

        try {
            //创建datastore
            params.put("hbase.catalog", "demo");
            params.put("hbase.zookeepers", "localhost");
            DataStore datastore = DataStoreFinder.getDataStore(params);

            // 创建schema
            SimpleFeatureType sft = demo01.getSimpleFeatureType();
            System.out.println(sft);
            //sft.getUserData().put("geomesa.mixed.geometries",Boolean.parseBoolean(true));
            datastore.createSchema(sft);

            //获取Features
            SimpleFeature feature = demo01.getData();

            //写入Features
            demo01.writeFeature(datastore, sft, feature);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这个方法主要用来将封装好的feature存入到对应的datastore中
     *
     * @param datastore 数据源，可以选择的有很多，此处是HBaseDataStore
     * @param sft       表结构
     * @param feature   封装好的一行数据
     */
    private void writeFeature(DataStore datastore, SimpleFeatureType sft, SimpleFeature feature) {
        try {
            System.out.println("write test data");
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                    datastore.getFeatureWriterAppend(sft.getTypeName(), Transaction.AUTO_COMMIT);
            SimpleFeature toWrite = writer.next();
            toWrite.setAttributes(feature.getAttributes());
            ((FeatureIdImpl) toWrite.getIdentifier()).setID(feature.getID());
            toWrite.getUserData().put(Hints.USE_PROVIDED_FID, Boolean.TRUE);
            toWrite.getUserData().putAll(feature.getUserData());
            writer.write();
            // 关闭流
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 这个方法主要是将非结构化的数据转换为feature对象
     *
     * @return feature对象
     */
    private SimpleFeature getData() {
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(getSimpleFeatureType());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
        builder.set("taxiId", "黑A SA2342");
        builder.set("dtg", Date.from(LocalDateTime.parse("2008-02-02 13:30:49", dateFormat).toInstant(ZoneOffset.UTC)));
        builder.set("geom", "POINT (116.31412 39.89454)");
        builder.set("description", "这是一辆套牌车");
        builder.featureUserData(Hints.USE_PROVIDED_FID, Boolean.TRUE);
        return builder.buildFeature("黑A SA2342");
    }
}
```

3、 测试读取表与数据。正常情况下代码执行不会有错误且执行完成后能看到打印相关数据。

```java
package com.ruqi.appserver.ruqi.geomesa;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import java.io.IOException;
import java.util.*;

public class DataStoreHbaseDemo {
    private static String catalog = "demo";
    //集群
    private static String zookeepers = "localhost";

    private Map getParams() {
        Map params = new HashMap();
        params.put("hbase.zookeepers", zookeepers);
        params.put("hbase.catalog", catalog);
        return params;
    }

    public void printfHbase() {
        try {
            DataStore dataStore = DataStoreFinder.getDataStore(getParams());
            if (dataStore != null) {
                //获取Catalog下所有的数据表
                String[] typeNames = dataStore.getTypeNames();
                if (typeNames.length > 0) {
                    //获取第一张数据表的数据信息
                    SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeNames[0]);
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DataStoreHbaseDemo dataStoreHbaseDemo = new DataStoreHbaseDemo();
        dataStoreHbaseDemo.printfHbase();
    }
}
```
