package com.ruqi.appserver.ruqi.geomesa.recommendpoint.base;

import org.opengis.feature.simple.SimpleFeature;

import java.util.List;

/**
 * 推荐点查询策略抽象接口
 *
 */
public interface IPointQueryStrategy {
      List<SimpleFeature>  queryRecommendPoints(double lng, double lat,String env);
}
