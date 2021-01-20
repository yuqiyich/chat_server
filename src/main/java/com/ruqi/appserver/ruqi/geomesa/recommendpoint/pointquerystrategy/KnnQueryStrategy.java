package com.ruqi.appserver.ruqi.geomesa.recommendpoint.pointquerystrategy;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoMesaUtil;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.geomesa.db.connect.MesaDataConnectManager;
import com.ruqi.appserver.ruqi.geomesa.recommendpoint.PointQueryMonitor;
import com.ruqi.appserver.ruqi.geomesa.recommendpoint.base.IPointQueryStrategy;
import it.geosolutions.imageio.utilities.MapEntry;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.locationtech.geomesa.features.ScalaSimpleFeature;
import org.locationtech.geomesa.process.query.KNearestNeighborSearchProcess;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;

import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.TABLE_RECORD_PRIMARY_KEY_PRECISION;
import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.WORLD_CODE;

/**
 * 根据上传的经纬度查询该点附近 100米范围内扎针点，然后通过KNN算法找到最近的扎针点，然后根据扎针点获取所有的推荐点
 */
public class KnnQueryStrategy implements IPointQueryStrategy {

    private KNearestNeighborSearchProcess mKNNprocess = new KNearestNeighborSearchProcess();
    @Override
    public List<SimpleFeature> queryRecommendPoints(double lng, double lat,String env) {
        String id = GeoMesaUtil.getPrecision(lng, TABLE_RECORD_PRIMARY_KEY_PRECISION) + "_" + GeoMesaUtil.getPrecision(lat, TABLE_RECORD_PRIMARY_KEY_PRECISION);
        //精准查询扎针点的关系表
        String recommonDatatableName = GeoTable.TABLE_RECOMMEND_DATA_PREFIX +env+ "_" + WORLD_CODE;
        DataStore dataStore = GeoDbHandler.getHbaseTableDataStore(recommonDatatableName);
        String typeName = MesaDataConnectManager.getIns().getTableTypeName(recommonDatatableName);
        if (dataStore != null && !StringUtils.isEmpty(typeName)) {
            try {
                String geoPointStr = "POINT(" + GeoMesaUtil.getPrecision(lng, TABLE_RECORD_PRIMARY_KEY_PRECISION) + " " + GeoMesaUtil.getPrecision(lat, TABLE_RECORD_PRIMARY_KEY_PRECISION) + ")";
                //FIXME ？？？此处的cql 在2021-01-20 加入了 dtg字段的过滤，因为不加时间的过滤会导致有一些fid一样的数据产生导致查询缓慢
                List<SimpleFeature> features = GeoDbHandler.queryFeature(dataStore,
                        Arrays.asList(new Query(typeName, ECQL.toFilter(" DWITHIN( sGeom , " + geoPointStr + " , 100 , meters ) and dtg DURING 2021-01-19T07:14:04.693Z/"+GeoMesaUtil.getGeoMesaTimeStr(new Date())))));
                List<SimpleFeature>  finalResult= RPHandleManager.getIns().getUniqueFidListAndDeleteMulitFidDatas(dataStore,typeName,recommonDatatableName,GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_DATA,features);
                //通过KNN算法算出最近的扎针点，取得扎针点的值
                SimpleFeature simpleFeature = filterByProcess(finalResult, geoPointStr);
                if (simpleFeature != null) {
                    MultiPoint points = (MultiPoint) simpleFeature.getAttribute("rGeoms");
                    //根据多点查具体信息
                    return queryRpPointsByID(points,env);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CQLException e) {
                e.printStackTrace();
            }

        } else {
            PointQueryMonitor.e("[" + recommonDatatableName + "] table not exists or schema is null by geomesa");
        }
        return null;
    }

    private SimpleFeature filterByProcess(List<SimpleFeature> features, String aimPoint) {
        SimpleFeature feature = null;
        if (features != null && features.size() > 0) {
            SimpleFeatureType sft = features.get(0).getFeatureType();
            DefaultFeatureCollection inputFeatures = new DefaultFeatureCollection(GeoTable.TYPE_RECOMMEND_POINT_ALL, sft);
            ScalaSimpleFeature inputsimpleFeature = covertPoint2Ssf(aimPoint, sft);
            inputFeatures.add(inputsimpleFeature);
            PointQueryMonitor.i("开始本地运算KNN算法");
            DefaultFeatureCollection dataSetFeatures = new DefaultFeatureCollection(GeoTable.TYPE_RECOMMEND_POINT_ALL, sft);
            if (features.size() > 0) {
                dataSetFeatures.addAll(features);
                SimpleFeatureCollection datas = mKNNprocess.execute(inputFeatures, dataSetFeatures, 1, 0d, 500d);
                try (SimpleFeatureIterator iterator = datas.features()) {
                    while (iterator.hasNext()) {
                        feature = iterator.next();
                        PointQueryMonitor.i("result " + DataUtilities.encodeFeature(feature));
                    }
                }
                PointQueryMonitor.i("结束本地运算KNN算法：结果为" + datas.size());
            }
        }
        return feature;
    }

    /**
     * @param aimPoint string  point的字符串  "POINT(113.3348 23.1067)"
     * @param sft
     * @return
     */
    private ScalaSimpleFeature covertPoint2Ssf(String aimPoint, SimpleFeatureType sft) {
        ScalaSimpleFeature builder = new ScalaSimpleFeature(sft, GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_DATA, null, null);
        String fid = UUID.randomUUID().toString();
        //23.107395,113.322317
        builder.setAttribute("rrId", fid);
        builder.setAttribute("lGeom", aimPoint);
        builder.setAttribute("sGeom", aimPoint);
        return builder;
    }

    private List<SimpleFeature> queryRpPointsByID(MultiPoint points, String env) {
        StringBuilder cqlPoint = new StringBuilder(GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_POINT + " IN (");
        int numPoints = points.getNumPoints();
        Coordinate[] coordinates = points.getCoordinates();
        for (int i = 0; i < numPoints; i++) {
            cqlPoint.append("'" + coordinates[i].x + "_" + coordinates[i].y + "'" + ((i == numPoints - 1) ? "" : ","));
        }
        cqlPoint.append(")");
        String recommonPointtableName = GeoTable.TABLE_RECOMMOND_PONIT_PREFIX + env + "_" + WORLD_CODE;
        String typeName = MesaDataConnectManager.getIns().getTableTypeName(recommonPointtableName);

        DataStore dataStorePoint = GeoDbHandler.getHbaseTableDataStore(recommonPointtableName);
        try {
            List<SimpleFeature> resultPoints= GeoDbHandler.queryFeature(dataStorePoint,
                    Arrays.asList(new Query(typeName, ECQL.toFilter(cqlPoint.toString()))));
            int resultPointsCount=resultPoints.size();
            if (numPoints==resultPointsCount){
                 // may bingo , point are exists ,and no mulit points todo may alse has same data?
            } else if (resultPointsCount>numPoints){
                //there many result more than expected,we should delete same data
                //check mulit data ,and delete it!!!
             return RPHandleManager.getIns().getUniqueFidListAndDeleteMulitFidDatas(dataStorePoint,typeName,recommonPointtableName,GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_POINT,resultPoints);
            }
            return resultPoints;
        } catch (IOException | CQLException e) {
            e.printStackTrace();
        }

        return null;

    }

}
