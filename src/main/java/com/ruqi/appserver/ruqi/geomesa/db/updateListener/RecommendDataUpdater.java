package com.ruqi.appserver.ruqi.geomesa.db.updateListener;

import com.ruqi.appserver.ruqi.geomesa.db.GeoDbHandler;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * 具体请对照表 {@link GeoTable#getRecommendRecordSimpleType(String, boolean)} }
 *  非record才会进入这个updater，更新一select的点为主键的表
 */
public class RecommendDataUpdater implements GeoDbHandler.IUpdateDataListener {

    @Override
    public void updateData(SimpleFeature oldData, SimpleFeature newData) {
        MultiPoint oldPoints= (MultiPoint) oldData.getAttribute("rGeoms");
        MultiPoint newPoints= (MultiPoint) newData.getAttribute("rGeoms");
        int oldChannel= (int) newData.getAttribute(GeoTable.KEY_CHANNEL);
        int newChannel= (int) oldData.getAttribute(GeoTable.KEY_CHANNEL);
        Set<Point> mergePoints = getMultiPoints(oldPoints);
        mergePoints.addAll(getMultiPoints(newPoints));
        Point[] arrays=new Point[mergePoints.size()];
        mergePoints.toArray(arrays);
        MultiPoint mergeMultiPoint=new MultiPoint(arrays,new GeometryFactory());
        oldData.setAttribute(GeoTable.KEY_CHANNEL,newChannel|oldChannel);
        oldData.setAttribute("rGeoms",mergeMultiPoint);
        //FIXME date attribute update may cause some new data,how to  fix?????
//        oldData.setAttribute(GeoTable.KEY_DATE,new Date(System.currentTimeMillis()));
        oldData.setAttribute(GeoTable.KEY_AD_CODE,newData.getAttribute(GeoTable.KEY_AD_CODE));
        if (oldData.getFeatureType().indexOf(GeoTable.KEY_CITY_CODE)>0){
            oldData.setAttribute(GeoTable.KEY_CITY_CODE,newData.getAttribute(GeoTable.KEY_CITY_CODE));
        }

    }

    private Set<Point> getMultiPoints(MultiPoint multiPoint){
        Set<Point> points=new HashSet<>();
        int pointsNum=multiPoint.getNumPoints();
        for (int i = 0; i < pointsNum; i++) {
            points.add((Point) multiPoint.getGeometryN(i));
        }
        return points;
    }
}
