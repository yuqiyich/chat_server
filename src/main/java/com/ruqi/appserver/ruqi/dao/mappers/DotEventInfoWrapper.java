package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.DotEventInfo;
import com.ruqi.appserver.ruqi.bean.RecordDotEventInfo;
import com.ruqi.appserver.ruqi.bean.RecordInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotEventInfoWrapper {

    @Insert("insert into dot_event_record(event_key,user_id,device_id,event_detail,create_time,device_brand,system_version,app_versionname,net_state,location_lat,location_lng,channel,app_id,app_versioncode,device_model,request_ip) " +
            "values(#{eventKey},#{userId},#{deviceId},#{eventDetail},#{createTime},#{deviceBrand},#{systemVersion},#{appVersionName},#{netState},#{locationLat},#{locationLng},#{channel},#{appId},#{appVersionCode},#{deviceModel},#{requestIp})")
    void insertDotEventRecord(DotEventInfo dotEventInfo);

    @Select({"<script>",
            "SELECT * FROM",
            "(SELECT * FROM dot_event_record",
            "WHERE 1=1",
            "<if test='dotEventInfo.content==null '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_APP')</if>",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName}</if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%')</if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%')</if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%')</if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey}</if>",
            "<if test='dotEventInfo.content.eventKey==null or dotEventInfo.content.eventKey==\"\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_APP')</if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND create_time &gt; #{dotEventInfo.content.startDate}</if>",
            "<if test='dotEventInfo.content.endDate!=null  '>AND create_time &lt; #{dotEventInfo.content.endDate}</if>",
            "</if>",
            "<if test='dotEventInfo.appInfo!=null and dotEventInfo.appInfo.appId!=null and dotEventInfo.appInfo.appId>0 '>AND app_id = #{dotEventInfo.appInfo.appId}</if>",
            "order by create_time desc) as a,",
            "app_info as b,risk_user as c ",
            " where a.app_id =b.app_id and a.user_id=c.user_id",
            "<if test='dotEventInfo.userInfo!=null and dotEventInfo.userInfo.userPhone!=null and dotEventInfo.userInfo.userPhone!=\"\" '>AND c.user_phone like concat('%', #{dotEventInfo.userInfo.userPhone}, '%')</if>",
            "order by create_time desc limit #{pageIndex}, #{limit}",
            "</script>"})
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "userPhone", column = "user_phone"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "deviceId", column = "device_id"),
            @Result(property = "eventDetail", column = "event_detail"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "appVersionName", column = "app_versionname"),
            @Result(property = "deviceBrand", column = "device_brand"),
            @Result(property = "deviceModel", column = "device_model"),
            @Result(property = "appVersionCode", column = "app_versioncode"),
            @Result(property = "netState", column = "net_state"),
            @Result(property = "locationLat", column = "location_lat"),
            @Result(property = "locationLng", column = "location_lng"),
            @Result(property = "scene", column = "scene"),
            @Result(property = "channel", column = "channel"),
            @Result(property = "ext", column = "ext"),
            @Result(property = "requestIp", column = "request_ip"),
            @Result(property = "eventKey", column = "event_key"),
            @Result(property = "systemVersion", column = "system_version")}
    )
    List<RecordDotEventInfo> queryEventRecmdPointListForLayui(int pageIndex, int limit, RecordInfo<DotEventInfo> dotEventInfo);

    @Select({"<script>",
            "SELECT count(*) FROM",
            "(SELECT * FROM dot_event_record",
            "WHERE 1=1",
            "<if test='dotEventInfo.content==null '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_APP')</if>",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName}</if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%')</if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%')</if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%')</if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND create_time &gt; #{dotEventInfo.content.startDate}</if>",
            "<if test='dotEventInfo.content.endDate!=null  '>AND create_time &lt; #{dotEventInfo.content.endDate}</if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey}</if>",
            "<if test='dotEventInfo.content.eventKey==null or dotEventInfo.content.eventKey==\"\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_APP')</if>",
            "</if>",
            "<if test='dotEventInfo.appInfo!=null and dotEventInfo.appInfo.appId!=null and dotEventInfo.appInfo.appId>0 '>AND app_id = #{dotEventInfo.appInfo.appId}</if>",
            ") as a,",
            "app_info as b,risk_user as c ",
            " where a.app_id =b.app_id and a.user_id=c.user_id",
            "<if test='dotEventInfo.userInfo!=null and dotEventInfo.userInfo.userPhone!=null and dotEventInfo.userInfo.userPhone!=\"\" '>AND c.user_phone like concat('%', #{dotEventInfo.userInfo.userPhone}, '%')</if>",
            "</script>"})
    /**
     * 不加temp报错：org.apache.ibatis.reflection.ReflectionException: There is no getter for property named "dotEventInfo"
     * 使用@Param("dotEventInfo")注解或者大于1个参数时不需要指定
     */
    long queryTotalSizeEventRecmdPoint(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo);
}
