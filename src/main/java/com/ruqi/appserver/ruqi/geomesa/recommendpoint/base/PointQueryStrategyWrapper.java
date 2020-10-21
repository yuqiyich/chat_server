package com.ruqi.appserver.ruqi.geomesa.recommendpoint.base;

import com.ruqi.appserver.ruqi.geomesa.recommendpoint.PointQueryMonitor;
import org.opengis.feature.simple.SimpleFeature;

import java.util.List;

public  class PointQueryStrategyWrapper implements  IPointQueryStrategy {
    protected IPointQueryStrategy pointQueryStrategy;

    public PointQueryStrategyWrapper(IPointQueryStrategy pointQueryStrategy) {
        this.pointQueryStrategy = pointQueryStrategy;
    }

    @Override
    public List<SimpleFeature> queryRecommendPoints(double lng, double lat,String env) {
        PointQueryMonitor.i("strategy["+pointQueryStrategy.getClass().getName()+"] start ");
        List<SimpleFeature> queryResults =  pointQueryStrategy.queryRecommendPoints(lng,lat,env);
        PointQueryMonitor.i("strategy["+pointQueryStrategy.getClass().getName()+"] end ");
        return queryResults;
    }
}
