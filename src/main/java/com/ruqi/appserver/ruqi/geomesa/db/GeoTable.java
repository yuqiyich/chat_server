package com.ruqi.appserver.ruqi.geomesa.db;

import com.ruqi.appserver.ruqi.utils.GeoStringBuilder;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * 推荐点的表的定义
 */
public class GeoTable {
    //推荐上车点的表
    public static final String TABLE_RECOMMOND_PONIT_PREFIX = "t_rpt_";
    //推荐上车点的原始记录表
    public static final String TABLE_RECOMMEND_RECORD_PREFIX = "t_rprt_";
    //以指针点的主键的关联多个推荐的实体表
    public static final String TABLE_RECOMMEND_DATA_PREFIX = "t_sprt_";
    //推荐上车点的原始记录中选择点和上车点的关系
    public static final String TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX = "t_rprrt_";

    //表类型
    public static final String TYPE_RECOMMEND_RECORD = "recommendRecord";
    public static final String TYPE_RECOMMEND_DATA = "recommendData";
    public static final String TYPE_RECOMMEND_POINT = "recommendPoint";
    public static final String TYPE_RECOMMEND_RELATED_RECORD = "recommendPointRelated";

    //主键key,在attr属性里面关联fid的数值
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_RELATED_RECORD = "rPRId";
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_DATA = "rrId";
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_POINT = "rpId";
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_RECORD = "rrId";

    //通用key字段
    /**
     * 渠道定义
     */
    public static final int CHANNEL_TX = 0b0000_0010;//腾训渠道
    public static final int CHANNEL_DIDI = 0b0000_0001;//滴滴渠道

    public static final String ATTR_KEY_CHANNEL = "channel:int";//来源渠道


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
    public static final String KEY_ADDRESS = "addressName";//获取推荐上车点的用户所选择的点的具体地址
    public static final String KEY_DATE = "dtg";//日期

    public static final int TABLE_RECORD_PRIMARY_KEY_PRECISION = 4;//小数点后保留4位(过滤记录表的主键精度)

    /**
     * 推荐点的记录表结构（方便数据回归,整理）
     * isRecord 为false 时，
     *
     * @param tableTailName (表的后缀名)
     * @param isRecord      是否是记录表
     * @return
     */
    public static SimpleFeatureType getRecommendRecordSimpleType(String tableTailName, boolean isRecord) {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append(PRIMARY_KEY_TYPE_RECOMMEND_DATA + ":String:index=true")
                .append(ATTR_KEY_CHANNEL)
                .append(ATTR_KEY_DATE)
                .append(ATTR_KEY_AD_CODE)
                .append(ATTR_KEY_CITY_CODE)
                //*表示建立索引，地理字段多个索引没用
                .append("lGeom:Point:srid=4326")//用户的定位点
                .append("*sGeom:Point:srid=4326")//获取推荐上车点的用户所选择的点// srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                .append("rGeoms:MultiPoint:srid=4326");//推荐点集合
        sft = SimpleFeatureTypes.createType((isRecord ? TYPE_RECOMMEND_RECORD : TYPE_RECOMMEND_DATA), attributes.toString());
        sft.getDescriptor("lGeom").getUserData().put("precision", "6");//设置地理属性的的精度，后面查询会用到
        sft.getDescriptor("sGeom").getUserData().put("precision", "6");//设置地理属性的的精度，后面查询会用到
        sft.getDescriptor("rGeoms").getUserData().put("precision", "6");//设置地理属性的的精度，后面查询会用到
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }

    /**
     * 推荐点的表结构
     *
     * @param tableTailName (表的后缀名)
     * @return
     */
    public static SimpleFeatureType getRecommendPointSimpleType(String tableTailName) {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append(PRIMARY_KEY_TYPE_RECOMMEND_POINT + ":String:index=true")//采用2点的id
                .append(ATTR_KEY_ADDRESS)
                .append(ATTR_KEY_TITLE)
                .append(ATTR_KEY_DATE)
                .append("updateTime:Integer:index=true")//更新次数
                //srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                .append("*rGeom:Point:srid=4326");//获取推荐上车点的用户所选择的点
        sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_POINT , attributes.toString());
        sft.getDescriptor("rGeom").getUserData().put("precision", "6"); //设置地理属性的的精度，后面查询会用到
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }

    /**
     * 用户指针点和某一个推荐点的关联表结构
     *
     * @param tableTailName (表的后缀名)
     * @return
     */
    public static SimpleFeatureType getRecommendRelatedSimpleType(String tableTailName) {
        GeoStringBuilder attributes = new GeoStringBuilder();
        SimpleFeatureType sft;
        attributes.append(PRIMARY_KEY_TYPE_RECOMMEND_RELATED_RECORD + ":String:index=true");
        attributes.append(PRIMARY_KEY_TYPE_RECOMMEND_POINT +":String:index=true");//推荐点的数据id
        attributes.append(PRIMARY_KEY_TYPE_RECOMMEND_RECORD +":String:index=true");//推荐点记录的id
        attributes.append(ATTR_KEY_DATE);
        sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_RELATED_RECORD, attributes.toString());
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }
}
