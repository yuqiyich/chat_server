package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.dbbean.*;
import com.ruqi.appserver.ruqi.bean.request.NewEventKeyRequest;
import com.ruqi.appserver.ruqi.bean.request.NewEventTypeRequest;
import com.ruqi.appserver.ruqi.bean.response.RecPointDayData;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DotEventInfoWrapper {

    @Insert("insert into event_type(type_key, type_key_name, remark, status, create_user_id, create_time)" +
            " values(#{eventType.typeKey}, #{eventType.typeKeyName}, #{eventType.remark}, 1," +
            " #{createUserId}, DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s'))")
    long insertDotEventType(@Param("eventType") NewEventTypeRequest request, @Param("createUserId") long createUserId);

    @Insert("insert into event_key(type_id, event_key, event_key_name, remark, status, create_user_id, create_time)" +
            " values(#{eventKey.typeId}, #{eventKey.eventKey}, #{eventKey.eventKeyName}, #{eventKey.remark}, 1," +
            " #{createUserId}, DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s'))")
    long insertDotEventKey(@Param("eventKey") NewEventKeyRequest newEventKeyRequest, @Param("createUserId") long createUserId);

    @Select({"<script>",
            "select event_type.id, type_key as typeKey, type_key_name as typeKeyName, remark, status,",
            " user.nickname as createUserName, event_type.create_time as createTime",
            " from event_type, user",
            " where event_type.create_user_id = user.id",
            "</script>"})
    List<DBEventType> getEventTypes();

    @Select({"<script>",
            "select event_key.id, type_id as typeId, event_key as eventKey, event_key_name as eventKeyName, remark, status,",
            " user.nickname as createUserName, event_key.create_time as createTime",
            " from event_key, user",
            " where event_key.create_user_id = user.id",
            "</script>"})
    List<DBEventKey> getEventKeys();

    @Insert("insert into dot_event_record(event_key,user_id,device_id,event_detail,create_time,record_time,device_brand," +
            "system_version,app_versionname,net_state,location_lat,location_lng,channel,app_id,platform,app_versioncode," +
            "device_model,request_ip, order_id, scene, user_type, ext) " +
            "values(#{dotEventInfo.content.eventKey},#{dotEventInfo.userInfo.userId},#{dotEventInfo.content.deviceId}," +
            "#{dotEventInfo.content.eventDetail},#{createDate},#{recordTime}," +
            "#{dotEventInfo.content.deviceBrand},#{dotEventInfo.content.systemVersion},#{dotEventInfo.content.appVersionName}," +
            "#{dotEventInfo.content.netState},#{dotEventInfo.content.locationLat},#{dotEventInfo.content.locationLng}," +
            "#{dotEventInfo.content.channel},#{appId},#{dotEventInfo.content.platform}," +
            "#{dotEventInfo.content.appVersionCode},#{dotEventInfo.content.deviceModel},#{requestIp}," +
            "#{dotEventInfo.content.orderId},#{dotEventInfo.content.scene},#{dotEventInfo.content.userType},#{dotEventInfo.content.ext})")
    void insertDotEventRecord(@Param("dotEventInfo") UploadRecordInfo<BaseUploadRecordInfo> uploadRecordInfo,
                              @Param("createDate") Date createDate,
                              @Param("appId") int appId,
                              @Param("recordTime") Date recordTime,
                              @Param("requestIp") String requestIp);

    @Select({"<script>",
            "SELECT count(*) FROM dot_event_record",
            "WHERE 1=1",
            "<if test='eventTypeStr!=null and eventTypeStr!=\"\"'>AND (${eventTypeStr}) </if>",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            "<if test='dotEventInfo.content.appId!=null and dotEventInfo.content.appId!=\"\" and dotEventInfo.content.appId>0'>AND app_id=#{dotEventInfo.content.appId}  </if>",
            "<if test='dotEventInfo.content.platform!=null and dotEventInfo.content.platform!=\"\" '>AND platform = #{dotEventInfo.content.platform} </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND record_time &gt; #{dotEventInfo.content.startDate}  </if>",
            "<if test='dotEventInfo.content.endDate!=null '>AND record_time &lt; #{dotEventInfo.content.endDate}  </if>",
            "<if test='dotEventInfo.content.scene!=null and dotEventInfo.content.scene!=\"\" '>AND scene = #{dotEventInfo.content.scene} </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"1\" '>AND order_id IS NOT NULL AND order_id!=''  </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"2\" '>AND (order_id IS NULL OR order_id='')  </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.content.userId}  </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName}  </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model=#{dotEventInfo.content.deviceModel} </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand=#{dotEventInfo.content.deviceBrand} </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id=#{dotEventInfo.content.deviceId} </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type = #{dotEventInfo.content.userType}  </if>",
            " </if>",
            "</script>"})
    /**
     * 不加temp报错：org.apache.ibatis.reflection.ReflectionException: There is no getter for property named "dotEventInfo"
     * 使用@Param("dotEventInfo")注解或者大于1个参数时不需要指定
     */
    long queryTotalSizeCommonEvent(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo, String eventTypeStr);

    @Select({"<script>",
            "SELECT a.*, event_key.event_key_name, b.app_name, c.totalSize FROM",
            "(SELECT * FROM dot_event_record",
            "WHERE 1=1",
            "<if test='eventTypeStr!=null and eventTypeStr!=\"\"'>AND (${eventTypeStr}) </if>",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\"'>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            "<if test='dotEventInfo.content.appId!=null and dotEventInfo.content.appId!=\"\" and dotEventInfo.content.appId>0'>AND app_id=#{dotEventInfo.content.appId}  </if>",
            "<if test='dotEventInfo.content.platform!=null and dotEventInfo.content.platform!=\"\" '>AND platform = #{dotEventInfo.content.platform} </if>",
            "<if test='dotEventInfo.content.startDate!=null'>AND record_time &gt; #{dotEventInfo.content.startDate} </if>",
            "<if test='dotEventInfo.content.endDate!=null'>AND record_time &lt; #{dotEventInfo.content.endDate} </if>",
            "<if test='dotEventInfo.content.scene!=null and dotEventInfo.content.scene!=\"\" '>AND scene = #{dotEventInfo.content.scene} </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"1\" '>AND order_id IS NOT NULL AND order_id!=''  </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"2\" '>AND (order_id IS NULL OR order_id='')  </if>",
            "<if test='dotEventInfo.content.orderId!=null and dotEventInfo.content.orderId!=\"\"'> AND order_id=#{dotEventInfo.content.orderId} </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.content.userId} </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName} </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model=#{dotEventInfo.content.deviceModel} </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand=#{dotEventInfo.content.deviceBrand} </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id=#{dotEventInfo.content.deviceId} </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type=#{dotEventInfo.content.userType} </if>",
            " </if>",
            "order by record_time desc limit #{pageIndex}, #{limit}) as a,",
            "app_info as b",
            ", (SELECT count(*) as totalSize FROM dot_event_record",
            "WHERE 1=1",
            "<if test='eventTypeStr!=null and eventTypeStr!=\"\"'>AND (${eventTypeStr}) </if>",
            "<if test='dotEventInfo.content!=null '>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            "<if test='dotEventInfo.content.appId!=null and dotEventInfo.content.appId!=\"\" and dotEventInfo.content.appId>0'>AND app_id=#{dotEventInfo.content.appId}  </if>",
            "<if test='dotEventInfo.content.platform!=null and dotEventInfo.content.platform!=\"\" '>AND platform = #{dotEventInfo.content.platform} </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND record_time &gt; #{dotEventInfo.content.startDate}  </if>",
            "<if test='dotEventInfo.content.endDate!=null '>AND record_time &lt; #{dotEventInfo.content.endDate}  </if>",
            "<if test='dotEventInfo.content.scene!=null and dotEventInfo.content.scene!=\"\" '>AND scene = #{dotEventInfo.content.scene} </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"1\" '>AND order_id IS NOT NULL AND order_id!=''  </if>",
            "<if test='dotEventInfo.content.orderIdFilter==\"2\" '>AND (order_id IS NULL OR order_id='')  </if>",
            "<if test='dotEventInfo.content.orderId!=null and dotEventInfo.content.orderId!=\"\"'> AND order_id=#{dotEventInfo.content.orderId} </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.content.userId}  </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName}  </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model=#{dotEventInfo.content.deviceModel} </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand=#{dotEventInfo.content.deviceBrand}  </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id=#{dotEventInfo.content.deviceId} </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type = #{dotEventInfo.content.userType}  </if>",
            "</if>",
            " ) as c",
            " , event_key",
            " where a.app_id =b.app_id and a.event_key = event_key.event_key;",
            "</script>"})
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "deviceId", column = "device_id"),
            @Result(property = "eventDetail", column = "event_detail"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "recordTime", column = "record_time"),
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
            @Result(property = "eventKeyName", column = "event_key_name"),
            @Result(property = "scene", column = "scene"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "systemVersion", column = "system_version"),
            @Result(property = "totalSize", column = "totalSize")}
    )
    List<RecordDotEventInfo> queryCommonEventListForLayui(int pageIndex, int limit,
                                                          RecordInfo<DotEventInfo> dotEventInfo, String eventTypeStr);

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
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model=#{dotEventInfo.content.deviceModel} </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand=#{dotEventInfo.content.deviceBrand} </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id=#{dotEventInfo.content.deviceId} </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND record_time &gt; #{dotEventInfo.content.startDate} </if>",
            "<if test='dotEventInfo.content.endDate!=null '>AND record_time &lt; #{dotEventInfo.content.endDate} </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type=#{dotEventInfo.content.userType} </if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            " </if>",
            "<if test='eventTypeStr!=null and eventTypeStr!=\"\"'>AND (${eventTypeStr}) </if>",
            "group by order_id, app_id) as ta",
            "</script>"})
    long queryEventTotalOrderSize(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo, String eventTypeStr);

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
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model=#{dotEventInfo.content.deviceModel} </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand=#{dotEventInfo.content.deviceBrand} </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id=#{dotEventInfo.content.deviceId} </if>",
            "<if test='dotEventInfo.content.startDate!=null '>AND record_time &gt; #{dotEventInfo.content.startDate} </if>",
            "<if test='dotEventInfo.content.endDate!=null '>AND record_time &lt; #{dotEventInfo.content.endDate} </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type=#{dotEventInfo.content.userType} </if>",
            "<if test='dotEventInfo.content.eventKey!=null and dotEventInfo.content.eventKey!=\"\" '>AND event_key = #{dotEventInfo.content.eventKey} </if>",
            " </if>",
            "<if test='eventTypeStr!=null and eventTypeStr!=\"\"'>AND (${eventTypeStr}) </if>",
            "group by user_id, app_id) as ta",
            "</script>"})
    long queryEventTotalUserSize(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo, String eventTypeStr);

    @Select({"<script>",
            "select date(record_time) as date, platform, event_key as eventKey, count(*) as totalCount",
            "  from dot_event_record",
            "where record_time >= date_sub(curdate(), interval 6 day)",
            "AND (event_key='H5_HYBRID_LOAD_SUCCESS' or event_key='H5_HYBRID_RELOAD_SUCCESS' ",
            "or event_key='H5_HYBRID_LOAD_FAIL' or event_key='H5_HYBRID_RELOAD_FAIL') AND app_id=1",
            "  group by date(record_time), platform, event_key",
            "</script>"})
    List<DBEventDayItemDataH5Hybrid> queryWeekDataH5Hybrid();

    @Select({"<script>",
            "select date(record_time) as date, platform, user_id as userId, count(*) as failCount",
            "from dot_event_record",
            "where record_time >= date_sub(curdate(), interval 6 day)",
            "AND (event_key='H5_HYBRID_LOAD_FAIL' or event_key='H5_HYBRID_RELOAD_FAIL') AND app_id=1",
            "group by date(record_time), platform, user_id ORDER BY count(*) DESC",
            "</script>"})
    List<DBEventDayDataH5Hybrid> queryWeekDataUserCountH5Hybrid();

    @Select({"<script>",
            " select date(record_time) as date, platform, event_key as eventKey, count(*) as totalCount",
            " from dot_event_record",
            " where record_time >= date_sub(curdate(), interval 6 day)",
            "<if test='isWithOrder == true'> AND order_id IS NOT NULL AND order_id!=''</if>",
            " AND (event_key='FALLBACK_SUCCESS_GAIA_RECOMMEND')",
            "<if test='appId!=null and appId!=\"\"'> AND app_id = #{appId}</if>",
            " group by date(record_time), platform, event_key",
            "</script>"})
    List<DBEventDayItemDataGaiaRecmd> queryWeekDataGaiaRecmd(@Param("appId") String appId, @Param("isWithOrder") boolean isWithOrder);

    @Select({"<script>",
            "SELECT * FROM",
            "(SELECT COUNT(*) as didi_total_count_android FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_DIDI_RECOMMEND' AND order_id is NULL ",
            "AND platform='Android') as dta,",
            "(SELECT COUNT(*) as didi_order_count_android FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_DIDI_RECOMMEND' AND order_id is NOT NULL ",
            "AND platform='Android') as doa,",
            "(SELECT COUNT(*) as didi_total_count_ios FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_DIDI_RECOMMEND' AND order_id is NULL ",
            "AND platform='iOS') as dti,",
            "(SELECT COUNT(*) as didi_order_count_ios FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_DIDI_RECOMMEND' AND order_id is NOT NULL ",
            "AND platform='iOS') as doi,",
            "(SELECT COUNT(*) as tencent_total_count_android FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_TX_RECOMMEND' AND order_id is NULL ",
            "AND platform='Android') as tta,",
            "(SELECT COUNT(*) as tencent_order_count_android FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_TX_RECOMMEND' AND order_id is NOT NULL ",
            "AND platform='Android') as toa,",
            "(SELECT COUNT(*) as tencent_total_count_ios FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_TX_RECOMMEND' AND order_id is NULL ",
            "AND platform='iOS') as tti,",
            "(SELECT COUNT(*) as tencent_order_count_ios FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_TX_RECOMMEND' AND order_id is NOT NULL ",
            "AND platform='iOS') as toi,",
            "(SELECT COUNT(*) as gaia_total_count_android FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_GAIA_RECOMMEND' AND order_id is NULL ",
            "AND platform='Android') as gta,",
            "(SELECT COUNT(*) as gaia_order_count_android FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_GAIA_RECOMMEND' AND order_id is NOT NULL ",
            "AND platform='Android') as goa,",
            "(SELECT COUNT(*) as gaia_total_count_ios FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_GAIA_RECOMMEND' AND order_id is NULL ",
            "AND platform='iOS') as gti,",
            "(SELECT COUNT(*) as gaia_order_count_ios FROM dot_event_record WHERE ",
            "record_time &gt; #{startTime} AND record_time &lt; #{endTime}",
            "AND app_id=#{appId} AND event_key='FALLBACK_SUCCESS_GAIA_RECOMMEND' AND order_id is NOT NULL ",
            "AND platform='iOS') as goi",
            "</script>"})
    DBEventDayDataRecPoint queryRecPointYesterdayData(@Param("appId") int appId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @Insert({"<script>",
            "insert into rec_point_event_statics(date, env, didi_total_count_android, didi_order_count_android, didi_total_count_ios, didi_order_count_ios,",
            " tencent_total_count_android, tencent_order_count_android, tencent_total_count_ios, tencent_order_count_ios,",
            " gaia_total_count_android, gaia_order_count_android, gaia_total_count_ios, gaia_order_count_ios)",
            " values(#{dbEventDayDataRecPoint.date}, #{dbEventDayDataRecPoint.env},",
            " #{dbEventDayDataRecPoint.didi_total_count_android}, #{dbEventDayDataRecPoint.didi_order_count_android}, #{dbEventDayDataRecPoint.didi_total_count_ios}, #{dbEventDayDataRecPoint.didi_order_count_ios},",
            " #{dbEventDayDataRecPoint.tencent_total_count_android}, #{dbEventDayDataRecPoint.tencent_order_count_android}, #{dbEventDayDataRecPoint.tencent_total_count_ios}, #{dbEventDayDataRecPoint.tencent_order_count_ios}, ",
            " #{dbEventDayDataRecPoint.gaia_total_count_android}, #{dbEventDayDataRecPoint.gaia_order_count_android}, #{dbEventDayDataRecPoint.gaia_total_count_ios}, #{dbEventDayDataRecPoint.gaia_order_count_ios})",
            "</script>"})
    void saveRecPointDayData(@Param("dbEventDayDataRecPoint") DBEventDayDataRecPoint dbEventDayDataRecPoint);

    @Select({"<script>",
            "select date, env, didi_total_count_android as didiTotalCountAndroid, didi_total_count_ios as didiTotalCountIOS, didi_order_count_android as didiOrderCountAndroid, didi_order_count_ios as didiOrderCountIOS,",
            " tencent_total_count_android as tencentTotalCountAndroid, tencent_total_count_ios as tencentTotalCountIOS, tencent_order_count_android as tencentOrderCountAndroid, tencent_order_count_ios as tencentOrderCountIOS,",
            " gaia_total_count_android as gaiaTotalCountAndroid, gaia_total_count_ios as gaiaTotalCountIOS, gaia_order_count_android as gaiaOrderCountAndroid, gaia_order_count_ios as gaiaOrderCountIOS",
            " from rec_point_event_statics",
            " where date >= date_sub(curdate(), interval 15 day)",
            " AND env = #{env} order by date",
            "</script>"})
    List<RecPointDayData> queryDayStaticsRecPointDatas(@Param("env") String env);
}
