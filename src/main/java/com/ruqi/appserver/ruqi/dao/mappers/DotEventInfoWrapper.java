package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventDayDataH5Hybrid;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventDayItemDataGaiaRecmd;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventDayItemDataH5Hybrid;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DotEventInfoWrapper {

    @Insert("insert into dot_event_record(event_key,user_id,device_id,event_detail,create_time,record_time,device_brand," +
            "system_version,app_versionname,net_state,location_lat,location_lng,channel,app_id,platform,app_versioncode," +
            "device_model,request_ip, order_id, scene, user_type) " +
            "values(#{dotEventInfo.content.eventKey},#{dotEventInfo.userInfo.userId},#{dotEventInfo.content.deviceId}," +
            "#{dotEventInfo.content.eventDetail},#{createDate},#{recordTime}," +
            "#{dotEventInfo.content.deviceBrand},#{dotEventInfo.content.systemVersion},#{dotEventInfo.content.appVersionName}," +
            "#{dotEventInfo.content.netState},#{dotEventInfo.content.locationLat},#{dotEventInfo.content.locationLng}," +
            "#{dotEventInfo.content.channel},#{appId},#{dotEventInfo.content.platform}," +
            "#{dotEventInfo.content.appVersionCode},#{dotEventInfo.content.deviceModel},#{requestIp}," +
            "#{dotEventInfo.content.orderId},#{dotEventInfo.content.scene},#{dotEventInfo.content.userType})")
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
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%')  </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%')  </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%')  </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type = #{dotEventInfo.content.userType}  </if>",
            " </if>",
            "</script>"})
    /**
     * 不加temp报错：org.apache.ibatis.reflection.ReflectionException: There is no getter for property named "dotEventInfo"
     * 使用@Param("dotEventInfo")注解或者大于1个参数时不需要指定
     */
    long queryTotalSizeCommonEvent(@Param("dotEventInfo") RecordInfo<DotEventInfo> dotEventInfo, String eventTypeStr);

    @Select({"<script>",
            "SELECT a.*, b.app_name, c.totalSize FROM",
//            "SELECT a.*, b.app_name FROM",
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
            "<if test='dotEventInfo.content.orderId!=null and dotEventInfo.content.orderId!=\"\"'> AND order_id like concat('%', #{dotEventInfo.content.orderId}, '%') </if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.content.userId} </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName} </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%') </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%') </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%') </if>",
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
            "<if test='dotEventInfo.content.orderId!=null and dotEventInfo.content.orderId!=\"\"'> AND order_id like concat('%', #{dotEventInfo.content.orderId}, '%')</if>",
            "<if test='dotEventInfo.content.userId!=null and dotEventInfo.content.userId!=\"\" '>AND user_id=#{dotEventInfo.content.userId}  </if>",
            "<if test='dotEventInfo.content.appVersionName!=null and dotEventInfo.content.appVersionName!=\"\" '>AND app_versionname = #{dotEventInfo.content.appVersionName}  </if>",
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%')  </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%')  </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%')  </if>",
            "<if test='dotEventInfo.content.userType!=0'>AND user_type = #{dotEventInfo.content.userType}  </if>",
            "</if>",
            " ) as c",
            " where a.app_id =b.app_id;",
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
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%') </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%') </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%') </if>",
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
            "<if test='dotEventInfo.content.deviceModel!=null and dotEventInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{dotEventInfo.content.deviceModel}, '%') </if>",
            "<if test='dotEventInfo.content.deviceBrand!=null and dotEventInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{dotEventInfo.content.deviceBrand}, '%') </if>",
            "<if test='dotEventInfo.content.deviceId!=null and dotEventInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{dotEventInfo.content.deviceId}, '%') </if>",
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
}
