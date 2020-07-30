package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.bean.GeoRecommendRelatedId;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.utils.GeoStringBuilder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.*;

import static com.ruqi.appserver.ruqi.geomesa.GeoTable.*;

/**
 * 普通数据与geomesa数据间的转化
 *
 */
public class GeoMesaDataWrapper {

    /**
     * 将推荐记录转化为geomesa的数据表中
     * 具体请对照表 {@link GeoTable#getRecommendRecordSimpleType()}
     *
     * @param records
     * @param sft
     * @return
     */
    public static SimpleFeature convertRecordToRecordSF(UploadRecommendPointRequest<RecommendPoint> records, SimpleFeatureType sft) {
        if (records!=null&&records.getRecommendPoint()!=null&&records.getRecommendPoint().size()>0){
            SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
            //23.107395,113.322317
            String selectPoint = "POINT ("+records.getSelectLng()  + " " +records.getSelectLat() + ")";
            StringBuilder mulitPoints = new StringBuilder();
            //对记录推荐点做一个多点存储
            int recommendPointsSize=records.getRecommendPoint().size();
            mulitPoints.append("MULTIPOINT (" );
            for (int i = 0; i < recommendPointsSize; i++) {
                RecommendPoint recommendPoint=records.getRecommendPoint().get(i);
                mulitPoints.append("(" + recommendPoint.getLng() + " "+ recommendPoint.getLat()+ ")"+(i==recommendPointsSize-1?"":","));
            }
            mulitPoints.append(")");
            UUID id = UUID.randomUUID();
            Date date = new Date(System.currentTimeMillis());
            builder.set("rrId", id.toString());
            builder.set(KEY_CHANNEL, records.getChannel());
            builder.set(KEY_DATE, date);
            builder.set(KEY_AD_CODE, records.getAdCode());
            builder.set(KEY_CITY_CODE, records.getCityCode());
            builder.set("lGeom", selectPoint);//用户的定位点
            builder.set("sGeom", selectPoint);//用户选择的点
            builder.set("rGeoms", mulitPoints);//给用户推荐的点
            return builder.buildFeature(id.toString());
        }
        return null;
    }

    /**
     * 将记录的数据中的某一个推荐点转化为geomeas的sf数据
     * 具体请对照表 {@link GeoTable#getRecommendPointSimpleType()}
     *
     * @param records
     * @param recommendPoint
     * @param sft
     * @return
     */
    public static SimpleFeature convertRecordToPointSF(UploadRecommendPointRequest<RecommendPoint> records, RecommendPoint recommendPoint, SimpleFeatureType sft) {
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
        UUID id = UUID.randomUUID();
        if (records != null && recommendPoint!=null) {
            Date date = new Date(System.currentTimeMillis());
            builder.set(KEY_DATE, date);
            builder.set(KEY_TITLE, recommendPoint.getTitle());
            builder.set(KEY_ADDRESS, recommendPoint.getAddress());
        }
        return builder.buildFeature(id.toString());
    }

    /**
     * 将记录的数据中的某一个推荐点和推荐记录转化为geomeas的sf数据
     * 具体请对照表 {@link GeoTable#getRecommendRelatedSimpleType()}
     *
     * @param records
     * @param sft
     * @return
     */
    public static SimpleFeature convertRecordToRelatedSF(GeoRecommendRelatedId records, SimpleFeatureType sft) {
        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
        UUID id = UUID.randomUUID();
        if (records != null) {
            Date date = new Date(System.currentTimeMillis());
            builder.set(KEY_DATE, date);
            builder.set("rPRId", id.toString());
            builder.set("rpId", records.getPointId());
            builder.set("rrId", records.getRecordId());
        }
        return builder.buildFeature(id.toString());
    }
}
