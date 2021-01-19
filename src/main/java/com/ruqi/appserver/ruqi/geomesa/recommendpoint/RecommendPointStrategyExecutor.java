package com.ruqi.appserver.ruqi.geomesa.recommendpoint;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.geomesa.recommendpoint.base.*;
import com.ruqi.appserver.ruqi.geomesa.recommendpoint.pointfilter.DefeatPointFilter;
import com.ruqi.appserver.ruqi.utils.BusinessException;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 推荐的查询策略执行者
 */
public class RecommendPointStrategyExecutor implements IPointQueryStrategy, IPointFilter {
    protected PointQueryConfig config;
    private final List<PointQueryStrategyWrapper> pointQueryStrategyWrappers = new ArrayList<>();
    private final LinkedList<PointFilterWrapper> pointFilterWrappers = new LinkedList<>();//串行数据
    private PointFilterChain pointFilterChain;

    public RecommendPointStrategyExecutor(PointQueryConfig config) throws BusinessException {
        if (config != null && !config.getPointQueryStrategys().isEmpty()) {
            List<IPointQueryStrategy> pointQueryStrategies = config.getPointQueryStrategys();
            List<IPointFilter> pointFilters = config.getPointFilters();
            for (IPointQueryStrategy pointQueryStrategy : pointQueryStrategies) {
                pointQueryStrategyWrappers.add(new PointQueryStrategyWrapper(pointQueryStrategy));
            }
            if (!pointFilters.isEmpty()) {
                for (IPointFilter pointFilter : pointFilters) {
                    pointFilterWrappers.add(new PointFilterWrapper(pointFilter));
                }
            }
            // add defeat filter
            pointFilterWrappers.add(0, new PointFilterWrapper(new DefeatPointFilter()));
            pointFilterChain = new PointFilterChain(pointFilterWrappers);
        } else {
            throw new BusinessException("no PointQueryStrategy ,can not create RecommendPointStrategyExecutor");
        }
    }

    @Override
    public List<SimpleFeature> doFilter(double lng, double lat, List<SimpleFeature> inputPoints, String env) {
        List<SimpleFeature> features = null;
        if (pointFilterChain != null && null != inputPoints && inputPoints.size() > 0) {
            features = pointFilterChain.filtersWork(lng, lat, inputPoints, env);
        }
        return features == null || features.size() == 0 ? inputPoints : features;
    }

    @Override
    public List<SimpleFeature> queryRecommendPoints(double lng, double lat, String env) {
        List<SimpleFeature> featuresResults = new ArrayList<>();
        //FIXME 多条策略：是否需要线程池多线程并发来提升效率？
        for (IPointQueryStrategy item : pointQueryStrategyWrappers) {
            List<SimpleFeature> simpleFeatures = item.queryRecommendPoints(lng, lat, env);
            if (null != simpleFeatures) {
                featuresResults.addAll(simpleFeatures);
            }
        }
        for (int i = featuresResults.size() - 1; i >= 0; i--) {
            SimpleFeature map = featuresResults.get(i);
            for (int j = i + 1; j < featuresResults.size(); j++) {
                SimpleFeature map2 = featuresResults.get(j);
                if (map.getAttribute(GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_POINT).equals(map2.getAttribute(GeoTable.PRIMARY_KEY_TYPE_RECOMMEND_POINT))) {
                    featuresResults.remove(i);
                    continue;
                }
            }
        }
        return featuresResults;
    }

    /**
     * 获取最佳的上车点
     */
    public List<RecommendPoint> queryBestRecommendPoints(double lng, double lat, String env) {
        PointQueryMonitor.i("point (" + lng + "," + lat + ") And [" + env + "] start strategy==");
        List<SimpleFeature> features = queryRecommendPoints(lng, lat, env);
        PointQueryMonitor.i("point (" + lng + "," + lat + ") And [" + env + "] exce end strategy. start filter points：" + (features != null ? features.size() : 0));
        List<SimpleFeature> featuresResults = doFilter(lng, lat, features, env);
        PointQueryMonitor.i("point (" + lng + "," + lat + ") And [" + env + "]filter end=== get RPpoints：" + (featuresResults != null ? featuresResults.size() : 0));
        //转换geo数据为业务数据
        if (featuresResults != null) {
            List<RecommendPoint> recommendPoints = new ArrayList<>();
            for (SimpleFeature simpleFeature :
                    featuresResults) {
                RecommendPoint recommendPoint = new RecommendPoint();
                String title = (String) simpleFeature.getAttribute(GeoTable.KEY_TITLE);
                String address = (String) simpleFeature.getAttribute(GeoTable.KEY_ADDRESS);
                Point point = (Point) simpleFeature.getAttribute(GeoTable.KEY_POINT_RECMD);
                recommendPoint.setLat(point.getY());
                recommendPoint.setLng(point.getX());
                recommendPoint.setTitle(title);
                recommendPoint.setAddress(address);
                recommendPoints.add(recommendPoint);
            }
            return recommendPoints;
        }
        return null;
    }
}
