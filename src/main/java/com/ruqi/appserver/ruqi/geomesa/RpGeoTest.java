package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.geomesa.db.*;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import com.ruqi.appserver.ruqi.utils.ThreadUtils;
import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Converter;
import org.locationtech.geomesa.features.ScalaSimpleFeature;
import org.locationtech.geomesa.index.conf.QueryHints;
import org.locationtech.geomesa.process.query.KNearestNeighborSearchProcess;
import org.locationtech.geomesa.utils.geotools.converters.JavaTimeConverterFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.text.DateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.*;

public class RpGeoTest {
    private static Logger logger = LoggerFactory.getLogger(RpGeoTest.class);
    public static final String CITY_CODE ="440100";//开发环境的citycode dev_440100
    public static final String Compose_CITY_CODE ="dev_"+CITY_CODE;


    /**
     * 初始化的城市列表
     *
     */
    public static final String[] INIT_TABLE_CITYS={"440100","440300","440600","441900","110000"};

    public static void main(String[] args) {
        GeoDbHandler.setDebug(true);
//        initAllTableIfNotExist(Arrays.asList(INIT_TABLE_CITYS));
//         RPHandleManager.getIns().batchSaveRecommendDatasByCityCode("dev",CITY_CODE,getListTestData());
         queryAllData("dev");
//           RPHandleManager.getIns().queryRecommendPoints(113.582381f, 22.751412,"pro");
//        RPHandleManager.getIns().queryRecommendPoints(113.3348123,23.1067123,"pro");
//        RPHandleManager.getIns().queryRecommendPoints(113.3352123,23.1064123,"pro");
//
//        try {
//            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(TABLE_RECOMMOND_PONIT_PREFIX+ "dev_"+GeoTable.WORLD_CODE),getTestQueries(TYPE_RECOMMEND_POINT_ALL));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        ExecutorService service= Executors.newFixedThreadPool(10);
//        for (int i = 0; i <3 ; i++) {
//            service.submit(new InsertTask());
//        }
    }

    public static  class InsertTask implements Runnable{

        @Override
        public void run() {
            RPHandleManager.getIns().batchSaveRecommendDatasByCityCode("dev",CITY_CODE,getListTestData());
        }
    }

    /**
     * 主要用于所有表的初始化，迁移数据使用确保所有数据表都存在
     * initAllTableIfNotExist
     * initAllTableIfNotExist(Arrays.asList(INIT_TABLE_CITYS));
     */
    public static void initAllTableIfNotExist(List<String> cityCodes){
        for (int i = 0; i < cityCodes.size(); i++) {
            RPHandleManager.getIns().saveRecommendDatasByCityCode("pro",cityCodes.get(i),getTestData());
        }
    }

   public static Query queryTableRowCount(String typeName){
       Query query = new Query(typeName);
       query.getHints().put(QueryHints.STATS_STRING(), "Count()");
//       System.out.println("dateStr----:"+ dateStr);
       String dateequal = "dtg DURING 2019-12-31T00:00:00.000Z/2020-10-18T00:00:00.000Z";
       try {
           query.setFilter(ECQL.toFilter(dateequal));
       } catch (CQLException e) {
           e.printStackTrace();
       }
       return query;
    }

    public static void queryAllData(String env){
//        try {
//            List<SimpleFeature> dataSets=GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_DATA_ALL));
//            logger.info("开始本地运算KNN算法");
//
//            SimpleFeatureType sft = dataSets.get(0).getFeatureType();
//            DefaultFeatureCollection inputFeatures = new DefaultFeatureCollection(GeoTable.TYPE_RECOMMEND_POINT_ALL,sft);
//            ScalaSimpleFeature inputsimpleFeature = convertPointToRecordSF(113.3348,23.1067,sft);
//            inputFeatures.add(inputsimpleFeature);
//            logger.info("开始本地运算KNN算法1");
//            DefaultFeatureCollection dataSetFeatures = new DefaultFeatureCollection(GeoTable.TYPE_RECOMMEND_POINT_ALL,sft);
//             if (dataSets!=null&&dataSets.size()>0){
//                 dataSetFeatures.addAll(dataSets);
//                 logger.info("开始本地运算KNN算法2");
//                 KNearestNeighborSearchProcess process=new KNearestNeighborSearchProcess();
//                SimpleFeatureCollection datas= process.execute(inputFeatures,dataSetFeatures,1,0d,500d);
//                 try ( SimpleFeatureIterator iterator = datas.features() ) {
//                     while (iterator.hasNext()) {
//                         SimpleFeature feature = iterator.next();
//                         logger.info("result " + DataUtilities.encodeFeature(feature));
//                     }
//                 }
//               logger.info("结束本地运算KNN算法：结果为"+datas.size());
//             }

//            //推荐点的表
            System.out.println("==============================推荐点的表=============================");
        try {
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+ Compose_CITY_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_POINT));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//            System.out.println("==============================推荐点的全局表=============================");
//            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_POINT_ALL));
////            System.out.println("数量是："+GeoDbHandler.queryTableRowCount(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+ Compose_CITY_CODE,null));  ;
//            //记录表
//            System.out.println("==============================记录表=============================");
//            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_RECORD));
//            //扎针点和上车点的表
//            System.out.println("==============================扎针点和上车点的表=============================");
//            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_DATA_ALL));
//            System.out.println("==============================扎针点和上车点的全局表=============================");
//            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_DATA_ALL));
//
//            //关系表
//            System.out.println("==============================关系表=============================");
//            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_RELATED_RECORD));

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static ScalaSimpleFeature convertPointToRecordSF(double lng, double lat, SimpleFeatureType sft) {
        String  fid = UUID.randomUUID().toString();
        ScalaSimpleFeature builder = new ScalaSimpleFeature(sft,"rrId",null,null);
            //23.107395,113.322317
            String selectPoint = "POINT ("+lng  + " " +lat + ")";
        builder.setAttribute("rrId",fid);
        builder.setAttribute("lGeom",selectPoint);
        builder.setAttribute("sGeom",selectPoint);

        return  builder;
    }

    public static List<Query> getTestQueries(String sftypeName) {
        List<Query> queries = new ArrayList<>();
            try {
                List<Query> query = new ArrayList<>();

                // most of the data is from 2018-01-01
                // note: DURING is endpoint exclusive
//                String during = "dtg DURING 2020-09-11T00:00:00.000Z/2020-09-11T03:00:00.000Z";
                String dateequal = "dtg DURING 2019-12-31T00:00:00.000Z/2022-06-02T00:00:00.000Z";
                String channel = "channel=2";
                //bbox rule  lng,lat,lng,lat
                String idrule="rpId = '113.33505_23.10702'";
                String bbox = "bbox(sGeom,113.11344,23.11344,113.11344,23.11344)";
                String equals=" EQUALS(sGeom,POINT(113.103284 23.120406))";
                String contains=" CONTAINS(sGeom,SRID=4326;POINT(113.3348 23.1067))";
                String exist="rrId EXISTS";
               String withIn=" DWITHIN( sGeom , POINT(113.3348 23.1067) , 100 , meters )";
//                query.add(new Query(GeoTable.TYPE_RECOMMEND_RECORD, ECQL.toFilter(idrule)));
                query.add(new Query(sftypeName, ECQL.toFilter(dateequal)));
//                query.add(queryTableRowCount(sftypeName));
//                query.add(new Query(GeoTable.TYPE_RECOMMEND_RECORD, ECQL.toFilter(contains+" AND " +during)));
                // bounding box over most of the united states
//                CONTAINS(geom,SRID=4326;POINT(113.98933 22.59750))

               /* String bbox = "bbox(geom,-120,30,-75,55)";
                 // POLYGON 必须起终点重合
                //"CONTAINS(POLYGON((0 0, 0 90, 90 90, 90 0, 0 0)), geom)"
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
            } catch (Exception e) {
                throw new RuntimeException("Error creating filter:", e);
            }
        return queries;
    }

    public static List<UploadRecommendPointRequest<RecommendPoint>> getListTestData(){
        List<UploadRecommendPointRequest<RecommendPoint>> dataSet= new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            dataSet.add(getTestData());
        }

        return dataSet;
    }


    public static UploadRecommendPointRequest<RecommendPoint>  getTestData(){
       UploadRecommendPointRequest<RecommendPoint>   testData= new UploadRecommendPointRequest<RecommendPoint>();
       testData.setCityCode(Compose_CITY_CODE);
       testData.setChannel(GeoTable.CHANNEL_TX);
       testData.setSelectLat(23.987651);
       testData.setSelectLng(123.987651);
       testData.setAdCode("11");
       testData.setUserLat(23.000001);
       testData.setUserLng(123.000001);
       testData.setRecommendPoint(getTestRecommendDatas());
       return testData;
    }

    private static List<RecommendPoint> getTestRecommendDatas() {
        List<RecommendPoint> data=new ArrayList<>();
        for (int i = 0; i < 2 ; i++) {
            RecommendPoint recommendPoint=new RecommendPoint();
            recommendPoint.setAddress("ruqi_test"+i);
            recommendPoint.setTitle("ruqi_test_title"+i);
            recommendPoint.setLat(23.10703);
            recommendPoint.setLng(113.33505);
            data.add(recommendPoint);
        }
        return data;
    }

    public static void testJdk8SStream(){
        List<RecommendPoint> datas=new ArrayList<>();
        for (int i = 0; i <5 ; i++) {
            RecommendPoint recommendPoint=new RecommendPoint();
            if (i<3){
                recommendPoint.setAddress("address");
            } else {
                recommendPoint.setAddress("address"+i);
            }
             datas.add(recommendPoint);
        }
        System.out.println(JsonUtil.beanToJsonStr(datas));
       List<RecommendPoint > dataChange = datas.stream().filter(distinctByKey(RecommendPoint::getAddress)).collect(Collectors.toList());
        System.out.println(JsonUtil.beanToJsonStr(dataChange));
    }


    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
