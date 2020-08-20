package com.ruqi.appserver.ruqi.geomesa.db;

import com.ruqi.appserver.ruqi.utils.GeoStringBuilder;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.KEY_CHANNEL;
import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.KEY_DATE;

/**
 * 测试geo的东西
 */
public class GeoTest {
    static List<Query> queries=null;
    public static void main(String[] args) {
        ExecutorService service= Executors.newFixedThreadPool(20);
        for (int i = 0; i <10000 ; i++) {
            service.submit(new SaveDataTask());
        }
//        queryData();
    }

  public static void   queryData(){
      try {
          GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_PONIT_RECORD),getTestQueries());
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
    public static List<Query> getTestQueries() {
        if (queries == null) {
            try {
                List<Query> query = new ArrayList<>();

                // most of the data is from 2018-01-01
                // note: DURING is endpoint exclusive
                String during = "dtg DURING 2019-12-31T00:00:00.000Z/2021-01-02T00:00:00.000Z";
//                String during = "channel='tx'";
                //bbox rule  lng,lat,lng,lat
                String idrule="rrId = 'a2ccc2b2-dec3-4405-8251-5df324cc34ee'";
                String bbox = "bbox(sGeom,113.11344,23.11344,113.11344,23.11344)";
                String equals=" EQUALS(sGeom,POINT(113.103284 23.120406))";
                String contains=" CONTAINS(sGeom,SRID=4326;POINT(113.103284 23.120406))";
                String exist="rrId EXISTS";
//                query.add(new Query(GeoTable.TYPE_RECOMMEND_RECORD, ECQL.toFilter(idrule)));
                query.add(new Query(GeoTable.TYPE_RECOMMEND_RECORD, ECQL.toFilter(idrule)));
//                query.add(new Query(GeoTable.TYPE_RECOMMEND_RECORD, ECQL.toFilter(contains+" AND " +during)));
                // bounding box over most of the united states
//                CONTAINS(geom,SRID=4326;POINT(113.98933 22.59750))

               /* String bbox = "bbox(geom,-120,30,-75,55)";

                // basic spatio-temporal query
                queries.add(new Query(getTypeName(), ECQL.toFilter(bbox + " AND " + during)));
                // basic spatio-temporal query with projection down to a few attributes
                queries.add(new Query(getTypeName(), ECQL.toFilter(bbox + " AND " + during),
                        new String[]{ "GLOBALEVENTID", "dtg", "geom" }));
                // attribute query on a secondary index - note we specified index=true for EventCode
                queries.add(new Query(getTypeName(), ECQL.toFilter("EventCode = '051'")));
                // attribute query on a secondary index with a projection
                queries.add(new Query(getTypeName(), ECQL.toFilter("EventCode = '051' AND " + during),
                        new String[]{ "GLOBALEVENTID", "dtg", "geom" }));*/

                queries = Collections.unmodifiableList(query);
            } catch (CQLException e) {
                throw new RuntimeException("Error creating filter:", e);
            }
        }
        return queries;
    }

  public static void   saveData(){
        //测试记录表的的数据
      DataStore dataStore = GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_PONIT_RECORD);
      if (dataStore!=null){
          SimpleFeatureType sft=GeoTable.getRecommendRecordSimpleType();
          List<SimpleFeature> datas=GeoTest.getRandomTestRecordDatas(sft);
          GeoDbHandler.createOrUpdateSchema(dataStore, sft);
          try {
              GeoDbHandler.writeNewFeaturesData(dataStore,sft,datas);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
    }


    public static List<SimpleFeature> getRandomTestRecordDatas(SimpleFeatureType sft) {
        List<SimpleFeature> datas = new ArrayList<>();
        for (int i = 0; i < 20000 ; i++) {
            datas.add(getTestRecordFeatureData(sft));
        }
        return datas;
    }

    private static SimpleFeature getTestRecordFeatureData(SimpleFeatureType sft) {
        //声明SimpleFeatureBuilder对象，用于创建feature，一个feature对应一条数据
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
        //23.107395,113.322317
        String selectPoint = "POINT "+getFormatTestPointData();
//        String mulitPoints = "MULTIPOINT (" + getFormatTestPointData() + "," + getFormatTestPointData() + "," + getFormatTestPointData() + "," + getFormatTestPointData() + "," + getFormatTestPointData() + "," + getFormatTestPointData() + "," + getFormatTestPointData() + ")";
        String mulitPoints = "MULTIPOINT ((122.987651 23.987651),(122.987652 23.987652),(122.987653 23.987653))";
        System.out.println("mulit:"+mulitPoints);
        Date date = new Date(System.currentTimeMillis());
        UUID id = UUID.randomUUID();
        builder.set("rrId", id.toString());
        builder.set(KEY_CHANNEL, "tx");
        builder.set(KEY_DATE, date);
        builder.set("sGeom", selectPoint);
        builder.set("rGeoms", mulitPoints);
        return builder.buildFeature(id.toString());
    }

    private static int getRandomInt() {
        return (new Random().nextInt(99999) + 100000);
    }

    private static String getFormatTestPointData() {
        return "(113." + getRandomInt() + " " + "23." + getRandomInt() + ")";
    }



  public static class SaveDataTask implements Runnable{


        @Override
        public void run() {
            GeoTest.saveData();
        }
    }

    private static class GeoTable{
        //推荐上车点的表
        public static  final String TABLE_RECOMMOND_PONIT="g_t_rpt";
        //推荐上车点的原始记录表
        public static  final String TABLE_SELECT_PONIT_RECORD="g_t_rprt";
        //推荐上车点的原始记录中选择点和上车点的关系
        public static  final String TABLE_SELECT_AND_RECOMMEND_RELATED="g_t_rprt";

        //表类型

        public static final String TYPE_RECOMMEND_RECORD = "recommendRecord";
        public static final String TYPE_RECOMMEND_POINT = "recommendPoint";
        public static final String TYPE_RECOMMEND_RELATED_RECORD = "recommendPointRelated";

        //通用key字段
        public static final String ATTR_KEY_CHANNEL = "channel:String";//来源渠道
        public static final String ATTR_KEY_CITY_CODE = "cityCode:String";//城市编码
        public static final String ATTR_KEY_AD_CODE = "adCode:String";//区域编码
        public static final String ATTR_KEY_TITLE = "title:String";//选择点的短地址名称
        public static final String ATTR_KEY_ADDRESS = "addressName:String";//获取推荐上车点的用户所选择的点的具体地址
        public static final String ATTR_KEY_DATE = "dtg:Date";//日期
        //通用属性
        public static final String KEY_CHANNEL = "channel";//来源渠道
        public static final String KEY_CITY_CODE = "cityCode";//城市编码
        public static final String KEY_AD_CODE = "adCode";//区域编码
        public static final String KEY_TITLE = "title";//选择点的短地址名称
        public static final String KEY_ADDRESS  = "addressName";//获取推荐上车点的用户所选择的点的具体地址
        public static final String KEY_DATE  = "dtg";//日期


        /**
         * 推荐点的记录表（方便数据回归,整理）
         *
         * @return
         */
        public static SimpleFeatureType getRecommendRecordSimpleType() {
            GeoStringBuilder attributes = new GeoStringBuilder();
            SimpleFeatureType sft;
            attributes.append("rrId:String:index=true")
                    .append(ATTR_KEY_CHANNEL)
                    .append(ATTR_KEY_DATE)
                    .append("*sGeom:Point:srid=4326")//获取推荐上车点的用户所选择的点// srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                    .append("*rGeoms:MultiPoint:srid=4326");//推荐点集合

            sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_RECORD, attributes.toString());
            sft.getDescriptor("sGeom").getUserData().put("precision", "6");//设置表的精度
            sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
            return sft;
        }

    }
}
