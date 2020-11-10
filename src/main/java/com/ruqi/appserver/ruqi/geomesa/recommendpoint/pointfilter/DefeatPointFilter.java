package com.ruqi.appserver.ruqi.geomesa.recommendpoint.pointfilter;

import com.ruqi.appserver.ruqi.geomesa.db.GeoTable;
import com.ruqi.appserver.ruqi.geomesa.recommendpoint.PointQueryMonitor;
import com.ruqi.appserver.ruqi.geomesa.recommendpoint.base.IPointFilter;
import org.locationtech.geomesa.utils.geohash.VincentyModel;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * a.根据点名字剔除重复的点
 * b.距离最相近的剔除法
 * 距离剔除法简要说明：
 *  1.随机取到列表L1中的某个点A，放到列表L2中
 *  2.遍历整个点列表L1计算与点A的距离，遍历到找到距离大于MAX_DISTANCE的第一个点B，将B放到列表L2中。
 *  3.继续遍历列表L1的过程，将L1剩余的点与L2中的A和B比较距离，距离大于MAX_DISTANCE则再次放入列表L2。
 *  4。直至循环L1结束，得出的来列表L2为最终结果表
 *
 */
public class DefeatPointFilter implements IPointFilter {

    public static double MAX_DISTANCE = 10;//单位米，最大的相近距离
    public static Random random = new Random();

    @Override
    public List<SimpleFeature> doFilter(double lng, double lat, List<SimpleFeature> inputPoints,String env) {
        //根据属性重名去除点位
        List<SimpleFeature> filterDatas = inputPoints.stream().filter(distinctByKey(b -> b.getAttribute(GeoTable.KEY_TITLE))).collect(Collectors.toList());
        //去除距离非常相近的点
        PointQueryMonitor.i("点集合距离相为[" + MAX_DISTANCE + "]之外计算开始 点数："+filterDatas.size());
        int originSize = filterDatas.size();
        int randomIndex = random.nextInt(originSize);
        List<SimpleFeature> distanceSimpleFeatures = new ArrayList<>();
        distanceSimpleFeatures.add(filterDatas.get(randomIndex));
        for (int i = 0; i < originSize; i++) {
            SimpleFeature aimSf = filterDatas.get(i);
            Geometry aimGeometry= (Geometry) aimSf.getDefaultGeometry();
            int filterSize=distanceSimpleFeatures.size();
            if (i == randomIndex) {
                continue;
            }
            boolean isInnerBreak = false;
            for (int j = 0; j < filterSize; j++) {
                SimpleFeature itemSf = distanceSimpleFeatures.get(j);
                Geometry itemGeometry= (Geometry) itemSf.getDefaultGeometry();
                double  distance=VincentyModel.getDistanceBetweenTwoPoints((Point)itemGeometry,(Point)aimGeometry).getDistanceInMeters();
//                PointQueryMonitor.d( "i:"+i+";j:"+j+"；距离为："+distance);
                if (distance < MAX_DISTANCE) {
                    //距离小于MAX_DISTANCE就直接break出去
                    isInnerBreak = true;
                    break;
                }
            }
            if (isInnerBreak){
                continue;
            }
            distanceSimpleFeatures.add(aimSf);
        }
        PointQueryMonitor.i("点集合距离随机相为[" + MAX_DISTANCE + "]之外计算结束:"+distanceSimpleFeatures.size());
        return distanceSimpleFeatures;
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
