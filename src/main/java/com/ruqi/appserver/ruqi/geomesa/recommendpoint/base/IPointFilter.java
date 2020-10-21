package com.ruqi.appserver.ruqi.geomesa.recommendpoint.base;

import org.opengis.feature.simple.SimpleFeature;

import java.util.List;

/**
 *
 * 对推荐点进行过滤
 */
public interface IPointFilter  {
    List<SimpleFeature>   doFilter(double lng, double lat,List<SimpleFeature> inputPoints,String env);
}
