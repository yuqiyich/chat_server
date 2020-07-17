package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.DotEventInfo;
import com.ruqi.appserver.ruqi.bean.RecordDotEventInfo;
import com.ruqi.appserver.ruqi.bean.RecordInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotEventInfoWrapper {

    @Insert("insert into dot_event_record(event_key,user_id,device_id,event_detail,create_time,device_brand,system_version,app_versionname,net_state,location_lat,location_lng,channel,app_id,platform,app_versioncode,device_model,request_ip, order_id, scene, user_type) " +
            "values(#{eventKey},#{userId},#{deviceId},#{eventDetail},#{createTime},#{deviceBrand},#{systemVersion},#{appVersionName},#{netState},#{locationLat},#{locationLng},#{channel},#{appId},#{platform},#{appVersionCode},#{deviceModel},#{requestIp},#{orderId},#{scene},#{userType})")
    void insertDotEventRecord(DotEventInfo dotEventInfo);

    @Select({"<script>",
            "SELECT count(*) FROM",
            "(SELECT * FROM dot_event_record",
            "WHERE 1=1",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.appId!=null and dotEventInfo.content.appId!=\"\" and dotEventInfo.content.appId>0'>AND app_id=#{dotEventInfo.content.appId}  </if>",
            "<if test='dotEventInfo.content.platform!=null and dotEventInfo.content.platform!=\"\" '>AND platform = #{dotEventInfo.content.platform} </if>",
            "<if test='dotEventInfo.content.scene!=null and dotEventInfo.content.scene!=\"\" '>AND scene = #{dotEventInfo.content.scene} </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"1\" '>AND order_id IS NOT NULL AND order_id!=''  </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"2\" '>AND (order_id IS NULL OR order_id='')  </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.userInfo.userPhone}  </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName}  </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%')  </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%')  </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%')  </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND create_time &gt; #{dotEventInfo.content.startDate}  </if>",
            "<if test='dotEventInfo.content.endDate!=null '>AND create_time &lt; #{dotEventInfo.content.endDate}  </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type = #{dotEventInfo.content.userType}  </if>",
            "<if test='dotEventInfo.content.eventType!=null'>",
            "<if test='dotEventInfo.content.eventType==\"nav\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_ROUTE_RETRY' or event_key = 'FALLBACK_SUCCESS_TX_ROUTE_CACHE' or event_key = 'FALLBACK_SUCCESS_ROUTE_GAODE' or event_key = 'FALLBACK_SUCCESS_ROUTE_BAIDU' or event_key = 'FALLBACK_SUCCESS_ROUTE_TENCENT' or event_key = 'FALLBACK_FAIL_ROUTE')  </if>",
            "<if test='dotEventInfo.content.eventType==\"recmdPoint\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_RECOMMEND' or event_key = 'FALLBACK_FAIL_BOARDING_POINT')  </if>",
            "<if test='dotEventInfo.content.eventType==\"driverLocation\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_TX_DEVICE_LOCATION' or event_key = 'FALLBACK_SUCCESS_DEVICE_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_APP_HISTORY_LOCATION' or event_key = 'FALLBACK_FAIL_LOCATION')  </if>",
            "<if test='dotEventInfo.content.eventType==\"endPoint\" '>AND (event_key = 'FB_SUC_DES_POP_SHOW' or event_key = 'FB_SUC_DES_POP_SELECT' or event_key = 'FB_SUC_DES_POP_ORDER' or event_key = 'FB_SUC_DES_LIST_SHOW' or event_key = 'FB_SUC_DES_LIST_SELECT' or event_key = 'FB_SUC_DES_LIST_ORDER')  </if>",
            " </if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            " </if>",
            ") as a,",
            "app_info as b ",
            " where a.app_id =b.app_id ",
            "</script>"})
    /**
     * 不加temp报错：org.apache.ibatis.reflection.ReflectionException: There is no getter for property named "dotEventInfo"
     * 使用@Param("dotEventInfo")注解或者大于1个参数时不需要指定
     */
    long queryTotalSizeCommonEvent(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo);

    @Select({"<script>",
            "SELECT * FROM",
            "(SELECT * FROM dot_event_record",
            "WHERE 1=1",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.appId!=null and dotEventInfo.content.appId!=\"\" and dotEventInfo.content.appId>0'>AND app_id=#{dotEventInfo.content.appId}  </if>",
            "<if test='dotEventInfo.content.platform!=null and dotEventInfo.content.platform!=\"\" '>AND platform = #{dotEventInfo.content.platform} </if>",
            "<if test='dotEventInfo.content.scene!=null and dotEventInfo.content.scene!=\"\" '>AND scene = #{dotEventInfo.content.scene} </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"1\" '>AND order_id IS NOT NULL AND order_id!=''  </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"2\" '>AND (order_id IS NULL OR order_id='')  </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.userInfo.userPhone} </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName} </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%') </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%') </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%') </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type=#{dotEventInfo.content.userType} </if>",
            "<if test='dotEventInfo.content.eventType!=null'>",
            "<if test='dotEventInfo.content.eventType==\"nav\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_ROUTE_RETRY' or event_key = 'FALLBACK_SUCCESS_TX_ROUTE_CACHE' or event_key = 'FALLBACK_SUCCESS_ROUTE_GAODE' or event_key = 'FALLBACK_SUCCESS_ROUTE_BAIDU' or event_key = 'FALLBACK_SUCCESS_ROUTE_TENCENT' or event_key = 'FALLBACK_FAIL_ROUTE')  </if>",
            "<if test='dotEventInfo.content.eventType==\"recmdPoint\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_RECOMMEND' or event_key = 'FALLBACK_FAIL_BOARDING_POINT')  </if>",
            "<if test='dotEventInfo.content.eventType==\"driverLocation\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_TX_DEVICE_LOCATION' or event_key = 'FALLBACK_SUCCESS_DEVICE_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_APP_HISTORY_LOCATION' or event_key = 'FALLBACK_FAIL_LOCATION')  </if>",
            "<if test='dotEventInfo.content.eventType==\"endPoint\" '>AND (event_key = 'FB_SUC_DES_POP_SHOW' or event_key = 'FB_SUC_DES_POP_SELECT' or event_key = 'FB_SUC_DES_POP_ORDER' or event_key = 'FB_SUC_DES_LIST_SHOW' or event_key = 'FB_SUC_DES_LIST_SELECT' or event_key = 'FB_SUC_DES_LIST_ORDER')  </if>",
            " </if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND create_time &gt; #{dotEventInfo.content.startDate} </if>",
            "<if test='dotEventInfo.content.endDate!=null  '>AND create_time &lt; #{dotEventInfo.content.endDate} </if>",
            " </if>",
            "order by create_time desc) as a,",
            "app_info as b ",
            " where a.app_id =b.app_id ",
            "order by create_time desc limit #{pageIndex}, #{limit}",
            "</script>"})
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "deviceId", column = "device_id"),
            @Result(property = "eventDetail", column = "event_detail"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "appVersionName", column = "app_versionname"),
            @Result(property = "deviceBrand", column = "device_brand"),
            @Result(property = "deviceModel", column = "device_model"),
            @Result(property = "appVersionCode", column = "app_versioncode"),
            @Result(property = "netState", column = "net_state"),
            @Result(property = "userType", column = "user_type"),
            @Result(property = "locationLat", column = "location_lat"),
            @Result(property = "locationLng", column = "location_lng"),
            @Result(property = "scene", column = "scene"),
            @Result(property = "channel", column = "channel"),
            @Result(property = "ext", column = "ext"),
            @Result(property = "requestIp", column = "request_ip"),
            @Result(property = "eventKey", column = "event_key"),
            @Result(property = "scene", column = "scene"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "systemVersion", column = "system_version")}
    )
    List<RecordDotEventInfo> queryCommonEventListForLayui(int pageIndex, int limit, RecordInfo<DotEventInfo> dotEventInfo);

//    @Select({"<script>",
//            "SELECT event_detail FROM dot_event_record",
//            "WHERE 1=1 AND platform = 'Android' ",
//            "<if test='key==\"nav\"'>AND (event_key = 'FALLBACK_SUCCESS_TX_ROUTE_RETRY' or event_key = 'FALLBACK_SUCCESS_TX_ROUTE_CACHE' or event_key = 'FALLBACK_SUCCESS_ROUTE_GAODE' or event_key = 'FALLBACK_SUCCESS_ROUTE_BAIDU' or event_key = 'FALLBACK_SUCCESS_ROUTE_TENCENT' or event_key = 'FALLBACK_FAIL_ROUTE')  </if>",
//            "<if test='key==\"recmdPoint\"'>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_RECOMMEND' or event_key = 'FALLBACK_FAIL_BOARDING_POINT')  </if>",
//            "<if test='key==\"driverLocation\"'>AND (event_key = 'FALLBACK_SUCCESS_TX_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_TX_DEVICE_LOCATION' or event_key = 'FALLBACK_SUCCESS_DEVICE_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_APP_HISTORY_LOCATION' or event_key = 'FALLBACK_FAIL_LOCATION')  </if>",
//            "<if test='key!=null and key!=\"\" and key!=\"nav\" and key!=\"recmdPoint\" and key!=\"driverLocation\"'>AND event_key=#{key} </if>",
//            "</script>"})
//    List<String> queryEventDetails(@Param("key") String key);

    @Select({"<script>",
            "SELECT count(*) FROM (SELECT order_id, app_id FROM dot_event_record ",
            "WHERE 1=1",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.appId!=null and dotEventInfo.content.appId!=\"\" and dotEventInfo.content.appId>0'>AND app_id=#{dotEventInfo.content.appId}  </if>",
            "<if test='dotEventInfo.content.platform!=null and dotEventInfo.content.platform!=\"\" '>AND platform = #{dotEventInfo.content.platform} </if>",
            "<if test='dotEventInfo.content.scene!=null and dotEventInfo.content.scene!=\"\" '>AND scene = #{dotEventInfo.content.scene} </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"1\" '>AND order_id IS NOT NULL AND order_id!=''  </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"2\" '>AND (order_id IS NULL OR order_id='')  </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.userInfo.userPhone} </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName} </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%') </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%') </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%') </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND create_time &gt; #{dotEventInfo.content.startDate} </if>",
            "<if test='dotEventInfo.content.endDate!=null '>AND create_time &lt; #{dotEventInfo.content.endDate} </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type=#{dotEventInfo.content.userType} </if>",
            "<if test='dotEventInfo.content.eventType!=null'>",
            "<if test='dotEventInfo.content.eventType==\"nav\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_ROUTE_RETRY' or event_key = 'FALLBACK_SUCCESS_TX_ROUTE_CACHE' or event_key = 'FALLBACK_SUCCESS_ROUTE_GAODE' or event_key = 'FALLBACK_SUCCESS_ROUTE_BAIDU' or event_key = 'FALLBACK_SUCCESS_ROUTE_TENCENT' or event_key = 'FALLBACK_FAIL_ROUTE')  </if>",
            "<if test='dotEventInfo.content.eventType==\"nav\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_ROUTE_RETRY' or event_key = 'FALLBACK_SUCCESS_TX_ROUTE_CACHE' or event_key = 'FALLBACK_SUCCESS_ROUTE_GAODE' or event_key = 'FALLBACK_SUCCESS_ROUTE_BAIDU' or event_key = 'FALLBACK_SUCCESS_ROUTE_TENCENT' or event_key = 'FALLBACK_FAIL_ROUTE')  </if>",
            "<if test='dotEventInfo.content.eventType==\"recmdPoint\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_RECOMMEND' or event_key = 'FALLBACK_FAIL_BOARDING_POINT')  </if>",
            "<if test='dotEventInfo.content.eventType==\"driverLocation\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_TX_DEVICE_LOCATION' or event_key = 'FALLBACK_SUCCESS_DEVICE_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_APP_HISTORY_LOCATION' or event_key = 'FALLBACK_FAIL_LOCATION')  </if>",
            "<if test='dotEventInfo.content.eventType==\"endPoint\" '>AND (event_key = 'FB_SUC_DES_POP_SHOW' or event_key = 'FB_SUC_DES_POP_SELECT' or event_key = 'FB_SUC_DES_POP_ORDER' or event_key = 'FB_SUC_DES_LIST_SHOW' or event_key = 'FB_SUC_DES_LIST_SELECT' or event_key = 'FB_SUC_DES_LIST_ORDER')  </if>",
            " </if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            " </if>",
            "group by order_id, app_id) as ta",
            "</script>"})
    long queryEventTotalOrderSize(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo);

    @Select({"<script>",
            "SELECT count(*) FROM (SELECT user_id, app_id FROM dot_event_record ",
            "WHERE 1=1",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.appId!=null and dotEventInfo.content.appId!=\"\" and dotEventInfo.content.appId>0'>AND app_id=#{dotEventInfo.content.appId}  </if>",
            "<if test='dotEventInfo.content.platform!=null and dotEventInfo.content.platform!=\"\" '>AND platform = #{dotEventInfo.content.platform} </if>",
            "<if test='dotEventInfo.content.scene!=null and dotEventInfo.content.scene!=\"\" '>AND scene = #{dotEventInfo.content.scene} </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"1\" '>AND order_id IS NOT NULL AND order_id!=''  </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"2\" '>AND (order_id IS NULL OR order_id='')  </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.userInfo.userPhone} </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName} </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%') </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%') </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%') </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND create_time &gt; #{dotEventInfo.content.startDate} </if>",
            "<if test='dotEventInfo.content.endDate!=null '>AND create_time &lt; #{dotEventInfo.content.endDate} </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type=#{dotEventInfo.content.userType} </if>",
            "<if test='dotEventInfo.content.eventType!=null'>",
            "<if test='dotEventInfo.content.eventType==\"nav\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_ROUTE_RETRY' or event_key = 'FALLBACK_SUCCESS_TX_ROUTE_CACHE' or event_key = 'FALLBACK_SUCCESS_ROUTE_GAODE' or event_key = 'FALLBACK_SUCCESS_ROUTE_BAIDU' or event_key = 'FALLBACK_SUCCESS_ROUTE_TENCENT' or event_key = 'FALLBACK_FAIL_ROUTE')  </if>",
            "<if test='dotEventInfo.content.eventType==\"recmdPoint\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_RECOMMEND' or event_key = 'FALLBACK_SUCCESS_TX_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_GEO' or event_key = 'FALLBACK_SUCCESS_RUQI_RECOMMEND' or event_key = 'FALLBACK_FAIL_BOARDING_POINT')  </if>",
            "<if test='dotEventInfo.content.eventType==\"driverLocation\" '>AND (event_key = 'FALLBACK_SUCCESS_TX_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_TX_DEVICE_LOCATION' or event_key = 'FALLBACK_SUCCESS_DEVICE_HISTORY_LOCATION' or event_key = 'FALLBACK_SUCCESS_APP_HISTORY_LOCATION' or event_key = 'FALLBACK_FAIL_LOCATION')  </if>",
            "<if test='dotEventInfo.content.eventType==\"endPoint\" '>AND (event_key = 'FB_SUC_DES_POP_SHOW' or event_key = 'FB_SUC_DES_POP_SELECT' or event_key = 'FB_SUC_DES_POP_ORDER' or event_key = 'FB_SUC_DES_LIST_SHOW' or event_key = 'FB_SUC_DES_LIST_SELECT' or event_key = 'FB_SUC_DES_LIST_ORDER')  </if>",
            " </if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            " </if>",
            "group by user_id, app_id) as ta",
            "</script>"})
    long queryEventTotalUserSize(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo);
}
