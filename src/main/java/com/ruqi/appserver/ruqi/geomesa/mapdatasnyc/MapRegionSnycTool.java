package com.ruqi.appserver.ruqi.geomesa.mapdatasnyc;

import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.SimpleFeatureBuilderWrapper;
import com.ruqi.appserver.ruqi.network.BaseHttpClient;
import com.ruqi.appserver.ruqi.utils.GeoStringBuilder;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.util.*;

import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.*;

/**
 * 行政区域的地图的存储的更新工具
 */
public class MapRegionSnycTool {



    //通用的表字段的定义
    private static final String ATTR_KEY_PARENT_CODE = "parentCode:Integer:index=true";//父级别的adcode
    private static final String ATTR_KEY_LEVEL = "level:String:index=true";//区域级别
    private static final String ATTR_KEY_AD_CODE = "adCode:String:index=true";//区域编码 ！！！！是主键id！！！！
    private static final String ATTR_KEY_TITLE = "adName:String";//区域名称
    private static final String ATTR_KEY_CENTER_POINT = "cGeom:Point:srid=4326";//区域的中心点
    private static final String ATTR_KEY_BOARD_MULTIPOLYGON = "*adBoard:MultiPolygon:srid=4326";//区域的边界
    private static final String ATTR_KEY_DATE = "dtg:Date";//日期

//    private static final String MAP_KEY = "3873816aea5f4cb9766df8d4914e3aef";
    private static final String MAP_KEY = "c281e5ceffa66f952052c38167abe63c";

    public static void main(String[] args) {

//        storeAdInfoByProvince("广东省");

//        queryAllData();

          storeChinaAdCodeInfo();
    }

    /**
     * 存储中国所有的行政区域的信息
     */
    public static District storeChinaAdCodeInfo() {
        String url = "https://restapi.amap.com/v3/config/district?keywords=中国&extensions=base&subdistrict=3&key=" + MAP_KEY;
        String res = BaseHttpClient.sendGet(url, "");
        System.out.println("res:" + res);
       return JsonUtil.jsonStrToBean(District.class, res);
    }



    /**
     * 请勿重复调用相同的省份 存了之后再下面标明：
     *  1.广东省
     *
     *
     *
     *
     * @param key  为adcode或者中文名 like  440000，广东省
     */
    private static void storeAdInfoByProvince(String key) {
        String url = "https://restapi.amap.com/v3/config/district?keywords="+key+"&extensions=all&subdistrict=1&key=" + MAP_KEY;
        List<District> nextLevels=putMapDataIntoGeoData(url,"100000");//中国的adcode为100000
        boolean isBreak=false;
        while (!isBreak||(nextLevels!=null&&nextLevels.size()>0)){
            if ("street".equals(nextLevels.get(0).level) ){//到街道就停止了
                isBreak=true;
            } else {
                List<District> datas=new ArrayList<>();
                for (District nextItem: nextLevels) {
                    List<District> nextparts=putMapDataIntoGeoData("https://restapi.amap.com/v3/config/district?keywords="+nextItem.adcode+"&extensions=all&subdistrict=1&key=" + MAP_KEY,nextItem.parentAdcode) ;
                    for (District child: nextparts) {
                        child.parentAdcode=nextItem.adcode;
                        datas.add(child);
                    }
                }
                nextLevels=datas;
            }
        }
    }

    /**
     *
     * @return  下一级别的dist数据
     */
    public static List<District> putMapDataIntoGeoData(String url,String parentAdcode){
        String res = BaseHttpClient.sendGet(url, "");
        System.out.println("res:" + res);
        District data = JsonUtil.jsonStrToBean(District.class, res);
        if ("1".equals(data.status)){
            for (District item: data.districts) {
                item.parentAdcode=parentAdcode;
            }
            saveDataInGeo(data.districts);
        }
        List<District> nextLevel=new ArrayList<>();
        for (District item:
                data.districts) {
            for (District child:
                    item.districts) {
                child.parentAdcode=item.adcode;
                nextLevel.add(child);
            }
        }
        return nextLevel;

    }

    public static void saveDataInGeo(List<District> input) {
        //测试记录表的的数据
        DataStore dataStore = GeoDbHandler.getHbaseTableDataStore(TABLE_ADMIN_DIVISION);
        if (dataStore != null) {
            SimpleFeatureType sft = getAdGeoType();
            List<SimpleFeature> datas = new ArrayList<>();
            for (int i = 0; i < input.size(); i++) {
                datas.add(convertToGeoSF(input.get(i), sft));
            }
            GeoDbHandler.createOrUpdateSchema(dataStore, sft);
            try {
                GeoDbHandler.writeNewFeaturesData(dataStore, sft, datas);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void queryAllData(){
        try {
            //推荐点的表
            System.out.println("==============================推荐点的表=============================");
            GeoDbHandler.setDebug(true);
            GeoDbHandler.queryFeature(GeoDbHandler.getHbaseTableDataStore(TABLE_RECOMMEND_DATA_PREFIX+"pro_"+"440600"),getTestQueries(TYPE_RECOMMEND_DATA));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SimpleFeature convertToGeoSF(District district, SimpleFeatureType sft) {
        SimpleFeatureBuilderWrapper builder = new SimpleFeatureBuilderWrapper(sft);
        if (district != null) {
            Date date = new Date(System.currentTimeMillis());
            builder.set("parentCode", district.parentAdcode);
            builder.set("level", district.level);
            builder.set("adCode", district.adcode);
            builder.set("adName", district.name);
            if (!StringUtils.isEmpty(district.center)) {
                String[] coors = district.center.split(",");
                builder.set("cGeom", "POINT(" + coors[0] + " " + coors[1] + ")");
            }
            if (district.polyline != null) {
                StringBuilder poliline = new StringBuilder();
                String[] polygons = district.polyline.split("\\|");
                poliline.append("MULTIPOLYGON(");
                if (polygons!=null&&!StringUtils.isEmpty(polygons[0])){
                    //添加其中一个多边形
                    System.out.println("多边形个数:"+polygons.length+"级别："+district.level);
                    for (int i = 0; i < polygons.length; i++) {
                        String[] coors = polygons[i].split(";");
                        poliline.append("((");

                        System.out.println("第【"+i+"】多边形点数point size:"+coors.length);
                        for (int j = 0; j < coors.length; j++) {
                            String[] points = coors[j].split(",");
                            poliline.append(points[0] + " " + points[1]+((j==coors.length-1)?"":"," ));
                        }
                        poliline.append("))");
                        if (i!=polygons.length-1){
                            poliline.append(",");
                        }
                    }
                }
                poliline.append(")");
                builder.set("adBoard", poliline.toString());

            }
            builder.set(KEY_DATE, date);
        }
        return builder.buildFeature(district.adcode);
    }

    /**
     * @return
     */
    public static SimpleFeatureType getAdGeoType() {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append(ATTR_KEY_PARENT_CODE)
                .append(ATTR_KEY_LEVEL)
                .append(ATTR_KEY_AD_CODE)
                .append(ATTR_KEY_TITLE)
                .append(ATTR_KEY_CENTER_POINT)
                .append(ATTR_KEY_BOARD_MULTIPOLYGON)
                .append(ATTR_KEY_DATE)
        ;
        //*表示建立索引，地理字段多个索引没用
        sft = SimpleFeatureTypes.createType((TYPE_ADMIN_DIVISION_META), attributes.toString());
        sft.getDescriptor("cGeom").getUserData().put("precision", "6");//设置地理属性的的精度，后面查询会用到
        sft.getDescriptor("adBoard").getUserData().put("precision", "6");//设置地理属性的的精度，后面查询会用到
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, "dtg");
        return sft;
    }


    public static class District {
        public String adcode;
        public String status;
        public String name;
        public String center;
        public String level;
        public List<District> districts;
        public String polyline;
        public String parentAdcode;
    }
//"north": double, 23.600684
//            * "east": double,113.046987
//            * "south": double,23.25015
//            * "west": double,113.297993



    /**
     * 113.046987 23.600684,113.046987 23.25015,113.297993 23.25015,113.297993 23.600684,113.046987 23.600684
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * 113.046987 23.25015
     * 113.297993 23.600684
     * @param sftypeName
     * @return
     */
    public static List<Query> getTestQueries(String sftypeName) {
        List<Query> queries = new ArrayList<>();
        try {
            List<Query> query = new ArrayList<>();

            // most of the data is from 2018-01-01
            // note: DURING is endpoint exclusive
            String during = "dtg DURING 2020-09-15T23:00:00.000Z/2021-11-02T00:00:00.000Z";
            String channel = "channel=2";
            //bbox rule  lng,lat,lng,lat
            String idrule="parentCode = '440100'";
            String bbox = "bbox(sGeom,113.11344,23.11344,113.11344,23.11344)";
            String equals=" EQUALS(sGeom,POINT(113.103284 23.120406))";
            String contains=" CONTAINS(sGeom,SRID=4326;POINT(113.103284 23.120406))";
            String exist="rrId EXISTS";
            //矩形值多边形的值
//            String  interect="OVERLAPS(adBoard,POLYGON((113.046987 23.600684,113.046987 23.25015,113.297993 23.25015,113.297993 23.600684,113.046987 23.600684)))  AND  level='district'";
//                query.add(new Query(GeoTable.TYPE_RECOMMEND_RECORD, ECQL.toFilter(idrule)));
//            query.add(new Query(sftypeName, ECQL.toFilter(during)));
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

}
