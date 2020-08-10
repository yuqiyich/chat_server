package com.ruqi.appserver.ruqi.geomesa.db;

import com.ruqi.appserver.ruqi.bean.GeoRecommendRelatedId;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.*;

import static com.ruqi.appserver.ruqi.geomesa.db.GeoTable.*;

/**
 * 普通数据与geomesa数据间的转化
 *
 */
public class GeoMesaDataWrapper {

    /**
     * 将推荐记录转化为geomesa的数据表中
     * 如果是记录表：具体请对照表 {@link GeoTable#getRecommendRecordSimpleType(String, boolean)}
     *
     * @param records
     * @param sft
     * @param isRecord 是否是记录表
     * @return
     */
    public static SimpleFeature convertRecordToRecordSF(UploadRecommendPointRequest<RecommendPoint> records, SimpleFeatureType sft,boolean isRecord) {
        if (records!=null&&records.getRecommendPoint()!=null&&records.getRecommendPoint().size()>0){
            SimpleFeatureBuilderWrapper builder = new SimpleFeatureBuilderWrapper(sft);
            //23.107395,113.322317
            String selectPoint = "POINT ("+records.getSelectLng()  + " " +records.getSelectLat() + ")";
            StringBuilder mulitPoints = new StringBuilder();
            //对记录推荐点做一个多点存储
            int recommendPointsSize=records.getRecommendPoint().size();
            mulitPoints.append("MULTIPOINT (" );
            for (int i = 0; i < recommendPointsSize; i++) {
                RecommendPoint recommendPoint=records.getRecommendPoint().get(i);
                mulitPoints.append("(" + recommendPoint.getLng() + " "+ recommendPoint.getLat()+ ")"+(i==(recommendPointsSize-1)?"":","));
            }
            mulitPoints.append(")");
            String fid="";
            if (isRecord){
                fid = UUID.randomUUID().toString();
            } else {//如果是非记录就以选择点为唯一id
                fid=GeoMathUtil.getPrecision(records.getSelectLng(),TABLE_RECORD_PRIMARY_KEY_PRECISION)  + "_" +GeoMathUtil.getPrecision(records.getSelectLat(),TABLE_RECORD_PRIMARY_KEY_PRECISION);
            }
            Date date = new Date(records.getTimeStamp()>0?records.getTimeStamp():System.currentTimeMillis());;
            builder.set("rrId", fid);
            builder.set(KEY_CHANNEL, records.getChannel());
            builder.set(KEY_DATE, date);
            builder.set(KEY_AD_CODE, records.getAdCode());
            builder.set(KEY_CITY_CODE, records.getCityCode());
            builder.set("lGeom", selectPoint);//用户的定位点
            builder.set("sGeom", selectPoint);//用户选择的点
            builder.set("rGeoms", mulitPoints.toString());//给用户推荐的点
            return builder.buildFeature(fid);
        }
        return null;
    }




    /**
     * 将记录的数据中的某一个推荐点转化为geomeas的sf数据
     * 具体请对照表 {@link GeoTable#getRecommendPointSimpleType(String)}
     *
     * @param records
     * @param recommendPoint
     * @param sft
     * @return
     */
    public static SimpleFeature convertRecordToPointSF(UploadRecommendPointRequest<RecommendPoint> records, RecommendPoint recommendPoint, SimpleFeatureType sft) {
        SimpleFeatureBuilderWrapper builder = new SimpleFeatureBuilderWrapper(sft);
        String id = recommendPoint.getLng() + "_" +recommendPoint.getLat();
        if (records != null)  {
            Date date = new Date(records.getTimeStamp()>0?records.getTimeStamp():System.currentTimeMillis());
            builder.set(KEY_DATE, date);
            builder.set(PRIMARY_KEY_TYPE_RECOMMEND_POINT, id);
            builder.set(KEY_TITLE, recommendPoint.getTitle());
            builder.set(KEY_ADDRESS, recommendPoint.getAddress());
            builder.set(KEY_UPDATE_COUNT, 0);//默认都是0次
            builder.set(KEY_CHANNEL, records.getChannel());//渠道
            builder.set("rGeom", "POINT("+recommendPoint.getLng()  + " " +recommendPoint.getLat()+")");
            builder.set(KEY_SHOT_COUNT, 0);//命中次数
            builder.set(KEY_EXT, "");//预留字段
        }
        return builder.buildFeature(id);
    }

    /**
     * 将记录的数据中的某一个推荐点和推荐记录关系转化为geomesa的sf数据
     * 具体请对照表 {@link GeoTable#getRecommendRelatedSimpleType(String)}
     *
     * @param records
     * @param sft
     * @return
     */
    public static SimpleFeature convertRecordToRelatedSF(GeoRecommendRelatedId records, SimpleFeatureType sft) {
        SimpleFeatureBuilderWrapper builder = new SimpleFeatureBuilderWrapper(sft);
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
