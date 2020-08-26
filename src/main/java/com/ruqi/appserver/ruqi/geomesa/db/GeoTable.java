package com.ruqi.appserver.ruqi.geomesa.db;

import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.utils.GeoStringBuilder;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 推荐点的各种表的定义
 * <p>
 * 注意如下几点：
 * 1.切记不要删除字段，会导致数据丢失，
 * 2.修改表只能增加字段，增加的同事也需考虑清楚索引问题，//todo 考虑索引问题
 * 且不能再原表中直接加
 * 3.userData可以随时修改
 */
@Component
public class GeoTable {
    //推荐上车点的表
    public static String TABLE_RECOMMOND_PONIT_PREFIX = "t_gaia_rpt_";
    //推荐上车点的原始记录表
    public static String TABLE_RECOMMEND_RECORD_PREFIX = "t_gaia_rprt_";
    //以指针点的主键的关联多个推荐的实体表
    public static String TABLE_RECOMMEND_DATA_PREFIX = "t_gaia_sprt_";
    //推荐上车点的原始记录中选择点和上车点的关系记录表
    public static String TABLE_SELECT_AND_RECOMMEND_RELATED_PREFIX = "t_gaia_rprrt_";
    //行政区域表
    public static String TABLE_ADMIN_DIVISION = "t_administrative_division";



    public static final String WORLD_CODE = "earth";//世界的adcode编码，自定义的

    //表类型
    public static final String TYPE_RECOMMEND_RECORD = "recommendRecord";
    public static final String TYPE_RECOMMEND_DATA = "recommendData";
    public static final String TYPE_RECOMMEND_DATA_ALL = "recommendData"+WORLD_CODE;
    public static final String TYPE_RECOMMEND_POINT = "recommendPoint";
    public static final String TYPE_RECOMMEND_POINT_ALL = "recommendPoint"+WORLD_CODE;
    public static final String TYPE_RECOMMEND_RELATED_RECORD = "recommendPointRelated";
    public static String TYPE_ADMIN_DIVISION_META = "m_administrative_division";
    //主键key,在attr属性里面关联fid的数值
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_RELATED_RECORD = "rPRId";
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_DATA = "rrId";
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_POINT = "rpId";
    public static final String PRIMARY_KEY_TYPE_RECOMMEND_RECORD = "rrId";


    /**
     * 渠道定义
     */
    public static final int CHANNEL_TX = 0b0000_0010;//腾讯sdk渠道
    public static final int CHANNEL_DIDI = 0b0000_0001;//滴滴sdk渠道
    //通用的表字段的定义
    public static final String ATTR_KEY_CHANNEL = "channel:Integer";//来源渠道
    public static final String ATTR_KEY_CITY_CODE = "cityCode:Integer:index=true";//城市编码
    public static final String ATTR_KEY_AD_CODE = "adCode:Integer";//区域编码
    public static final String ATTR_KEY_TITLE = "title:String";//选择点的短地址名称
    public static final String ATTR_KEY_ADDRESS = "addressName:String";//获取推荐上车点的用户所选择的点的具体地址
    public static final String ATTR_KEY_DATE = "dtg:Date";//日期
    public static final String ATTR_KEY_SHOT_COUNT = "shotCount:Integer";//命中得分（这里指用户的选择了推荐点的上车点）
    public static final String ATTR_KEY_UPDATE_COUNT = "updateTime:Integer:index=true";//更新的次数
    public static final String ATTR_KEY_EXT = "ext:String";//额外字段
    //通用key字段名
    public static final String KEY_CHANNEL = "channel";//来源渠道
    public static final String KEY_CITY_CODE = "cityCode";//城市编码
    public static final String KEY_AD_CODE = "adCode";//区域编码
    public static final String KEY_TITLE = "title";//选择点的短地址名称
    public static final String KEY_ADDRESS = "addressName";//获取推荐上车点的用户所选择的点的具体地址
    public static final String KEY_DATE = "dtg";//日期
    public static final String KEY_SHOT_COUNT = "shotCount";//命中得分
    public static final String KEY_UPDATE_COUNT = "updateTime";//更新次数
    public static final String KEY_EXT = "ext";//命中得分

    public static final String KEY_POINT_RECMD = "rGeom";// 推荐点坐标
    public static final String KEY_POINT_ORIGIN = "sGeom";// 原始扎针点坐标

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
                //*表示建立索引，地理字段多个索引没用
                .append("lGeom:Point:srid=4326")//用户的定位点
                .append("*sGeom:Point:srid=4326")//获取推荐上车点的用户所选择的点// srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                .append("rGeoms:MultiPoint:srid=4326");//推荐点集合
        if (isRecord){//记录表需要加citycode
            attributes.append(ATTR_KEY_CITY_CODE);
        } else {
            if (tableTailName.contains(WORLD_CODE)){//全量表就要加cityCode
                attributes.append(ATTR_KEY_CITY_CODE);
            } else{
                //其他以城市区分的就不需要citycode字段
            }
        }
        sft = SimpleFeatureTypes.createType((isRecord ? TYPE_RECOMMEND_RECORD : tableTailName.contains(WORLD_CODE)? TYPE_RECOMMEND_DATA_ALL:TYPE_RECOMMEND_DATA), attributes.toString());
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
                .append(ATTR_KEY_UPDATE_COUNT)//更新次数
                .append(ATTR_KEY_CHANNEL)//渠道分析
                //srid是GIS当中的一个空间参考标识符。而此处的srid=4326表示这些数据对应的WGS 84空间参考系统
                .append("*rGeom:Point:srid=4326")//获取推荐上车点的用户所选择的点
                .append(ATTR_KEY_AD_CODE)//获取推荐上车点的用户所选择的点
                .append(ATTR_KEY_EXT)//获取推荐上车点的用户所选择的点
                .append(ATTR_KEY_SHOT_COUNT);//获取推荐上车点的用户所选择的点
        if (tableTailName.contains(WORLD_CODE)){//全量表就要加cityCode
            attributes.append(ATTR_KEY_CITY_CODE);
        }

        sft = SimpleFeatureTypes.createType(tableTailName.contains(WORLD_CODE)?TYPE_RECOMMEND_POINT_ALL:TYPE_RECOMMEND_POINT, attributes.toString());
        sft.getDescriptor("rGeom").getUserData().put("precision", "6"); //设置地理属性的的精度，后面查询会用到
        sft.getDescriptor(KEY_AD_CODE).getUserData().put("index", "true"); //增加adCode的索引
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
        attributes.append(PRIMARY_KEY_TYPE_RECOMMEND_POINT + ":String:index=true");//推荐点的数据id
        attributes.append(PRIMARY_KEY_TYPE_RECOMMEND_RECORD + ":String:index=true");//推荐点记录的id
        attributes.append(ATTR_KEY_DATE);
        sft = SimpleFeatureTypes.createType(TYPE_RECOMMEND_RELATED_RECORD, attributes.toString());
        sft.getUserData().put(SimpleFeatureTypes.DEFAULT_DATE_KEY, KEY_DATE);
        return sft;
    }

//    public static SimpleFeatureType getAddAttrSFT(String typeName) {
//        GeoStringBuilder attrStringBuilder = new GeoStringBuilder();
//        if (TYPE_RECOMMEND_POINT.equals(typeName)) {
//            attrStringBuilder = addRPAttrV1(null);
//        }
//        return SimpleFeatureTypes.createType(typeName, attrStringBuilder.toString());
//    }
}
