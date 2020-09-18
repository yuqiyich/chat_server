package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoMesaUtil;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Converter;
import org.locationtech.geomesa.index.conf.QueryHints;
import org.locationtech.geomesa.utils.geotools.converters.JavaTimeConverterFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

public class RpGeoTest {
    public static final String CITY_CODE ="441900";//开发环境的citycode dev_440100
    public static final String Compose_CITY_CODE ="pro_"+CITY_CODE;


    /**
     * 初始化的城市列表
     *
     */
    public static final String[] INIT_TABLE_CITYS={"440100","440300","440600","441900"};

    public static void main(String[] args) {
        GeoDbHandler.setDebug(true);
//         RPHandleManager.getIns().saveRecommendDatasByCityCode("pro",CITY_CODE,getTestData());
//         queryAllData("pro");
           RPHandleManager.getIns().queryRecommendPoints(113.582381f, 22.751412f,440100,"pro");
    }

    /**
     * 主要用于所有表的初始化，迁移数据使用确保所有数据表都存在
     * initAllTableIfNotExist
     * initAllTableIfNotExist(Arrays.asList(INIT_TABLE_CITYS));
     */
    public static void initAllTableIfNotExist(List<String> cityCodes){
        for (int i = 0; i < cityCodes.size(); i++) {
            RPHandleManager.getIns().saveRecommendDatasByCityCode("dev",cityCodes.get(i),getTestData());
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
        try {
            //推荐点的表
            System.out.println("==============================推荐点的表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+ Compose_CITY_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_POINT));
            System.out.println("==============================推荐点的全局表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_POINT_ALL));
//            System.out.println("数量是："+GeoDbHandler.queryTableRowCount(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+ Compose_CITY_CODE,null));  ;
            //记录表
            System.out.println("==============================记录表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_RECORD));
            //扎针点和上车点的表
            System.out.println("==============================扎针点和上车点的表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX+ Compose_CITY_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_DATA));
            System.out.println("==============================扎针点和上车点的全局表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_DATA_ALL));

            //关系表
            System.out.println("==============================关系表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX+ env+"_"+GeoTable.WORLD_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_RELATED_RECORD));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Query> getTestQueries(String sftypeName) {
        List<Query> queries = new ArrayList<>();
            try {
                List<Query> query = new ArrayList<>();

                // most of the data is from 2018-01-01
                // note: DURING is endpoint exclusive
                String during = "dtg DURING 2020-09-11T00:00:00.000Z/2020-09-11T03:00:00.000Z";
                String dateequal = "dtg DURING 2019-12-31T00:00:00.000Z/2022-01-02T00:00:00.000Z";
                String channel = "channel=2";
                //bbox rule  lng,lat,lng,lat
                String idrule="rpId = '122.984662_23.986662'";
                String bbox = "bbox(sGeom,113.11344,23.11344,113.11344,23.11344)";
                String equals=" EQUALS(sGeom,POINT(113.103284 23.120406))";
                String contains=" CONTAINS(sGeom,SRID=4326;POINT(113.103284 23.120406))";
                String exist="rrId EXISTS";
//                query.add(new Query(GeoTable.TYPE_RECOMMEND_RECORD, ECQL.toFilter(idrule)));
                query.add(new Query(sftypeName, ECQL.toFilter(during)));
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
        for (int i = 0; i < 1 ; i++) {
            RecommendPoint recommendPoint=new RecommendPoint();
            recommendPoint.setAddress("ruqi_test"+i);
            recommendPoint.setTitle("ruqi_test_title"+i);
            recommendPoint.setLat(GeoMesaUtil.getPrecisionDouble(23.111111+0.000001*(i+1),6));
            recommendPoint.setLng(GeoMesaUtil.getPrecisionDouble(122.111111+0.000001*(i+1),6));
            data.add(recommendPoint);
        }
        return data;
    }
}
