package com.ruqi.appserver.ruqi.geomesa;

import com.ruqi.appserver.ruqi.utils.GeoStringBuilder;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * 推荐点的表名
 *
 */
public class GeoTable {
    //推荐上车点的表
    public static  final String TABLE_RECOMMOND_PONIT="t_rpt";
    //推荐上车点的原始记录表
    public static  final String TABLE_SELECT_PONIT_RECORD="t_rprt";
    //推荐上车点的原始记录中选择点和上车点的关系
    public static  final String TABLE_SELECT_AND_RECOMMEND_RELATED="t_rprt";

    //表类型

    public static final String TYPE_RECOMMEND_RECORD = "recommendRecord";
    public static final String TYPE_RECOMMEND_POINT = "recommendPoint";
    public static final String TYPE_RECOMMEND_RELATED_RECORD = "recommendPointRelated";

    //通用key字段
    public static final String ATTR_KEY_CHANNEL = "channel:String";//来源渠道
    public static final String ATTR_KEY_CITY_CODE = "cityCode:String";//城市编码
    public static final String ATTR_KEY_AD_CODE = "adCode:String";//区域编码
    public static final String ATTR_KEY_TITLE = "title:String";//选择点的短地址名称
    public static final String ATTR_KEY_ADDRESS = "addressName:String";//获取推荐上车点的用户所选择的点的具体地址
    public static final String ATTR_KEY_DATE = "dtg:Date";//日期
    //通用属性
    public static final String KEY_CHANNEL = "channel";//来源渠道
    public static final String KEY_CITY_CODE = "cityCode";//城市编码
    public static final String KEY_AD_CODE = "adCode";//区域编码
    public static final String KEY_TITLE = "title";//选择点的短地址名称
    public static final String KEY_ADDRESS  = "addressName";//获取推荐上车点的用户所选择的点的具体地址
    public static final String KEY_DATE  = "dtg";//日期


    /**
     * 推荐点的记录表（方便数据回归,整理）
     *
     * @return
     */
    public static SimpleFeatureType getRecommendRecordSimpleType() {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append("rrId:String:index=true")
                .append(ATTR_KEY_CHANNEL)
                .append(ATTR_KEY_DATE)
                .append(ATTR_KEY_AD_CODE)
                .append(ATTR_KEY_CITY_CODE)
                .append("lGeom:Point:srid=4326")//用户的定位点
                .append("*sGeom:Point:srid=4326")//获取推荐上车点的用户所选择的点// srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                .append("rGeoms:MultiPoint:srid=4326");//推荐点集合
        sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_RECORD, attributes.toString());
        sft.getDescriptor("sGeom").getUserData().put("precision", "8");//设置地理属性的的精度，后面查询会用到
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }

    /**
     * 以指针点的为唯一id来插入数据的
     *
     * @return
     */
    public static SimpleFeatureType getRecommendDataSimpleType() {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append("rrpId:String:index=true")
                .append(ATTR_KEY_CHANNEL)
                .append(ATTR_KEY_DATE)
                .append(ATTR_KEY_AD_CODE)
                .append(ATTR_KEY_CITY_CODE)
                .append("lGeom:Point:srid=4326")//用户的定位点
                .append("*sGeom:Point:srid=4326")//获取推荐上车点的用户所选择的点// srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                .append("rGeoms:MultiPoint:srid=4326");//推荐点集合
        sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_RECORD, attributes.toString());
        sft.getDescriptor("sGeom").getUserData().put("precision", "8");//设置地理属性的的精度，后面查询会用到
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }

    /**
     * 推荐点的表
     *
     * @return
     */
    public static SimpleFeatureType getRecommendPointSimpleType() {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append("rpId:String:index=true")//采用2点的id
                .append(ATTR_KEY_TITLE)
                .append(ATTR_KEY_ADDRESS)
                .append(ATTR_KEY_DATE)
                .append("updateTime:int")
                //srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                .append("*rGeom:Point:srid=4326");//获取推荐上车点的用户所选择的点
        sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_POINT, attributes.toString());
        sft.getDescriptor("rGeom").getUserData().put("precision", "8"); //设置地理属性的的精度，后面查询会用到
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }

    /**
     * 用户指针点和某一个推荐点的关联表
     *
     * @return
     */
    public static SimpleFeatureType getRecommendRelatedSimpleType() {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append("rPRId:String:index=true,");
        attributes.append("rpId:String:index=true");//推荐点的数据id
        attributes.append("rrId:String:index=true");//推荐点记录的id
        sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_RELATED_RECORD, attributes.toString());
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }
}
