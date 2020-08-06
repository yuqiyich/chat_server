package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoMathUtil;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.geomesa.index.conf.QueryHints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RpGeoTest {
    public static final String CITY_CODE ="440100";//开发环境的citycode dev_440100
    public static final String Compose_CITY_CODE ="dev_"+CITY_CODE;

    public static void main(String[] args) {
         RPHandleManager.getIns().saveRecommendDatasByCityCode("dev",CITY_CODE,getTestData());
         queryAllData();
    }
   public static Query queryTableRowCount(String typeName){
       Query query = new Query(typeName);
       query.getHints().put(QueryHints.STATS_STRING(), "Count()");
       return query;
    }

    public static void queryAllData(){
        try {
            //推荐点的表
            System.out.println("==============================推荐点的表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMOND_PONIT_PREFIX+ Compose_CITY_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_POINT));
            //记录表
            System.out.println("==============================记录表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_RECORD_PREFIX+ Compose_CITY_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_RECORD));
            //扎针点和上车点的表
            System.out.println("==============================扎针点和上车点的表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_RECOMMEND_DATA_PREFIX+ Compose_CITY_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_DATA));
            //关系表
            System.out.println("==============================关系表=============================");
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(GeoTable.TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX+ Compose_CITY_CODE),getTestQueries(GeoTable.TYPE_RECOMMEND_RELATED_RECORD));

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static List<Query> getTestQueries(String sftypeName) {
        List<Query> queries = new ArrayList<>();
            try {
                List<Query> query = new ArrayList<>();

                // most of the data is from 2018-01-01
                // note: DURING is endpoint exclusive
                String during = "dtg DURING 2020-05-31T00:00:00.000Z/2020-11-02T00:00:00.000Z";
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
                query.add(queryTableRowCount(sftypeName));
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
        return queries;
    }


    public static UploadRecommendPointRequest<RecommendPoint>  getTestData(){
       UploadRecommendPointRequest<RecommendPoint>   testData= new UploadRecommendPointRequest<RecommendPoint>();
       testData.setCityCode(Compose_CITY_CODE);
       testData.setChannel(GeoTable.CHANNEL_TX);
       testData.setSelectLat(23.987651);
       testData.setSelectLng(123.987651);
       testData.setAdCode(Compose_CITY_CODE);
       testData.setUserLat(23.987652);
       testData.setUserLng(123.987652);
       testData.setRecommendPoint(getTestRecommendDatas());
       return testData;
    }

    private static List<RecommendPoint> getTestRecommendDatas() {
        List<RecommendPoint> data=new ArrayList<>();
        for (int i = 1; i < 4 ; i++) {
            RecommendPoint recommendPoint=new RecommendPoint();
            recommendPoint.setAddress("更改后地址"+i);
            recommendPoint.setTitle("更改后title"+i);
            recommendPoint.setLat(GeoMathUtil.getPrecisionDouble(23.98665+0.000001*(i+1),6));
            recommendPoint.setLng(GeoMathUtil.getPrecisionDouble(122.98465+0.000001*(i+1),6));
            data.add(recommendPoint);
        }
        return data;
    }
}
