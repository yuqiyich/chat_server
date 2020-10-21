package com.ruqi.appserver.ruqi.geomesa.recommendpoint.base;

import com.ruqi.appserver.ruqi.geomesa.recommendpoint.PointQueryMonitor;
import org.opengis.feature.simple.SimpleFeature;

import java.util.List;

/**
 *
 *对推荐点进行过滤
 */
public  class PointFilterWrapper  implements  IPointFilter {
    protected IPointFilter pointFilter;

    public PointFilterWrapper(IPointFilter decoratedShape){
        this.pointFilter = decoratedShape;
    }

    @Override
    public List<SimpleFeature> doFilter(double lng, double lat, List<SimpleFeature> inputPoints,String env) {
        PointQueryMonitor.i("point filter ["+pointFilter.getClass().getName()+"] start ");
        List<SimpleFeature> results= pointFilter.doFilter(lng,lat,inputPoints,env);
        PointQueryMonitor.i("point filter ["+pointFilter.getClass().getName()+"] end ");
        return results;
    }

}
