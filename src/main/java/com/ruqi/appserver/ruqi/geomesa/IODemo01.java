package com.ruqi.appserver.ruqi.geomesa;

import org.geotools.data.DataStore;
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
import java.util.Locale;

public class IODemo01 {

    public static void main(String[] args) {
        IODemo01 demo01 = new IODemo01();

        try {
            //创建datastore
            DataStore datastore = GeoMesaHBaseConfig.getTestDemoHBaseDataStore();

            // 创建schema
            SimpleFeatureType sft = demo01.getSimpleFeatureType();
            System.out.println("--->sft:" + sft);
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
     * 这个方法主要设定了表名GeoMesaHBaseConfig.HBASE_DEMO_TYPE_VALUE，
     * 和schema结构"taxiId:String,dtg:Date,*geom:Point:srid=4326"
     *
     * @return SimpleFeatureType，即建表的schema表结构
     */
    public SimpleFeatureType getSimpleFeatureType() {
        SimpleFeatureType sft = SimpleFeatureTypes.createType(GeoMesaHBaseConfig.HBASE_DEMO_TYPE_VALUE, "taxiId:String,dtg:Date,*geom:Point:srid=4326,description:String");
        //sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, "dtg");
        //设置xz3索引时间间隔为天
        sft.getUserData().put("geomesa.xz3.interval", "day");
        //设置时空索引时间字段为date
        sft.getUserData().put("geomesa.index.dtg", "dateAttr");
        //设置索引精度
        sft.getUserData().put("geomesa.xz.precision", 10);
        return sft;
    }

    /**
     * 这个方法主要用来将封装好的feature存入到对应的datastore中
     *
     * @param datastore 数据源，可以选择的有很多，此处是HBaseDataStore
     * @param sft       表结构
     * @param feature   封装好的一行数据
     */
    private void writeFeature(DataStore datastore, SimpleFeatureType sft, SimpleFeature feature) {
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer = null;
        try {
            System.out.println("--->write test data");
            writer =
                    datastore.getFeatureWriterAppend(sft.getTypeName(), Transaction.AUTO_COMMIT);
            SimpleFeature toWrite = writer.next();
            toWrite.setAttributes(feature.getAttributes());
            ((FeatureIdImpl) toWrite.getIdentifier()).setID(feature.getID());
            toWrite.getUserData().put(Hints.USE_PROVIDED_FID, Boolean.TRUE);
            toWrite.getUserData().putAll(feature.getUserData());
            writer.write();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                // 关闭流
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writer = null;
            }
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

        // 向Geotools确认要用用户自行设定的id
        builder.featureUserData(Hints.USE_PROVIDED_FID, Boolean.TRUE);
        // 这里设定一个ID，使用的是具体的某一个值
        return builder.buildFeature("黑A SA2342");
    }
}
