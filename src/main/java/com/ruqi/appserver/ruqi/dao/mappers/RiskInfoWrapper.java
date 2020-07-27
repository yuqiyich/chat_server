package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.bean.RecordRiskInfo;
import com.ruqi.appserver.ruqi.bean.RiskInfo;
import com.ruqi.appserver.ruqi.bean.RiskOverviewInfo;
import com.ruqi.appserver.ruqi.dao.entity.DeviceRiskOverviewEntity;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RiskInfoWrapper {

    @Insert("insert into risk_record(risk_type,user_id,device_id,risk_detail,create_time,record_time,device_brand,system_version,app_versionname,net_state,location_lat,location_lng,channel,app_id,app_versioncode,device_model,scene,request_ip) " +
            "values(#{riskType},#{userId},#{deviceId},#{riskDetail},#{createTime},#{recordTime},#{deviceBrand},#{systemVersion},#{appVersionName},#{netState},#{locationLat},#{locationLng},#{channel},#{appId},#{appVersionCode},#{deviceModel},#{scene},#{requestIp})")
    int insert(RiskInfo riskInfo);

    @Select("select count(*) from risk_record where create_time > #{startTime} and create_time < #{endTime} and app_id=#{appId}")
    int countSecurityNum(int appId, Date startTime, Date endTime);

    @Select("select count(DISTINCT(user_id)) from risk_record where create_time > #{startTime} and create_time < #{endTime} and app_id=#{appId}")
    int countSecurityUserNum(int appId, Date startTime, Date endTime);

//    @Select({"<script>",
//            "SELECT * FROM",
//            "(SELECT * FROM risk_record",
//            "WHERE 1=1",
//            "<if test='riskInfo.content!=null and riskInfo.content.appVersionName!=null and riskInfo.content.appVersionName!=\"\" '>AND app_versionname = #{riskInfo.content.appVersionName}</if>",
//            "<if test='riskInfo.content!=null and riskInfo.content.deviceModel!=null and riskInfo.content.deviceModel!=\"\" '>AND device_model = #{riskInfo.content.deviceModel}</if>",
//            "<if test='riskInfo.content!=null and riskInfo.content.deviceBrand!=null and riskInfo.content.deviceBrand!=\"\" '>AND device_brand = #{riskInfo.content.deviceBrand}</if>",
//            "<if test='riskInfo.appInfo!=null and riskInfo.appInfo.appId!=null and riskInfo.appInfo.appId>0 '>AND app_id = #{riskInfo.appInfo.appId}</if>",
//            "<if test='riskInfo.content!=null and riskInfo.content.startDate!=null '>AND create_time &gt; #{riskInfo.content.startDate}</if>",
//            "<if test='riskInfo.content!=null and riskInfo.content.endDate!=null  '>AND create_time &lt; #{riskInfo.content.endDate}</if>",
//            "order by create_time desc",
//            "limit #{pageIndex}, #{limit}) as a,",
//            "app_info as b,risk_user as c ",
//            " where a.app_id =b.app_id and a.user_id=c.user_id and a.app_id=c.app_id ",
//            "order by create_time desc",
//            "</script>"})
//    @Results({@Result(property = "userInfo.userId", column = "user_id"),
//            @Result(property = "userInfo.nickName", column = "nick_name"),
//            @Result(property = "userInfo.userPhone", column = "user_phone"),
//            @Result(property = "appInfo.appId", column = "app_id"),
//            @Result(property = "appInfo.appName", column = "app_name"),
//            @Result(property = "content.appId", column = "app_id"),
//            @Result(property = "content.userId", column = "user_id"),
//            @Result(property = "content.deviceId", column = "device_id"),
//            @Result(property = "content.riskDetail", column = "risk_detail"),
//            @Result(property = "content.createTime", column = "create_time"),
//            @Result(property = "content.appVersionName", column = "app_versionname"),
//            @Result(property = "content.deviceBrand", column = "device_brand"),
//            @Result(property = "content.appVersionCode", column = "app_versioncode"),
//            @Result(property = "content.netState", column = "net_state"),
//            @Result(property = "content.locationLat", column = "location_lat"),
//            @Result(property = "content.locationLng", column = "location_lng"),
//            @Result(property = "content.scene", column = "scene"),
//            @Result(property = "content.channel", column = "channel"),
//            @Result(property = "content.ext", column = "ext"),
//            @Result(property = "content.systemVersion", column = "system_version")}
//    )
//    List<RecordInfo<RiskInfo>> queryRiskList(int pageIndex, int limit, RecordInfo<RiskInfo> riskInfo);

    @Select({"<script>",
            "SELECT * FROM",
            "(SELECT * FROM risk_record",
            "WHERE 1=1",
            "<if test='riskInfo.appInfo!=null and riskInfo.appInfo.appId!=null and riskInfo.appInfo.appId>0 '>AND app_id = #{riskInfo.appInfo.appId}</if>",
            "<if test='riskInfo.content!=null'>",
            "<if test='riskInfo.content.appVersionName!=null and riskInfo.content.appVersionName!=\"\" '>AND app_versionname = #{riskInfo.content.appVersionName}</if>",
            "<if test='riskInfo.content.deviceModel!=null and riskInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{riskInfo.content.deviceModel}, '%')</if>",
            "<if test='riskInfo.content.deviceBrand!=null and riskInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{riskInfo.content.deviceBrand}, '%')</if>",
            "<if test='riskInfo.content.deviceId!=null and riskInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{riskInfo.content.deviceId}, '%')</if>",
            "<if test='riskInfo.content.userId!=\"\" '>AND user_id=#{riskInfo.content.userId}</if>",
            "<if test='riskInfo.content.riskType!=null and riskInfo.content.riskType!=\"\" '>AND risk_type like concat('%', #{riskInfo.content.riskType}, '%')</if>",
            "<if test='riskInfo.content.startDate!=null '>AND create_time &gt; #{riskInfo.content.startDate}</if>",
            "<if test='riskInfo.content.endDate!=null  '>AND create_time &lt; #{riskInfo.content.endDate}</if>",
            "</if>",
            "order by create_time desc limit #{pageIndex}, #{limit}) as a,",
            "app_info as b,risk_user as c ",
            "where a.app_id =b.app_id and a.user_id=c.user_id and a.app_id=c.app_id ",
            "order by create_time desc",
            "</script>"})
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "userPhone", column = "user_phone"),
//            @Result(property = "appId", column = "app_id"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "deviceId", column = "device_id"),
            @Result(property = "riskDetail", column = "risk_detail"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "appVersionName", column = "app_versionname"),
            @Result(property = "deviceBrand", column = "device_brand"),
            @Result(property = "deviceModel", column = "device_model"),
            @Result(property = "appVersionCode", column = "app_versioncode"),
            @Result(property = "netState", column = "net_state"),
            @Result(property = "locationLat", column = "location_lat"),
            @Result(property = "locationLng", column = "location_lng"),
            @Result(property = "scene", column = "scene"),
            @Result(property = "recordTime", column = "record_time"),
            @Result(property = "channel", column = "channel"),
            @Result(property = "ext", column = "ext"),
            @Result(property = "requestIp", column = "request_ip"),
            @Result(property = "riskType", column = "risk_type"),
            @Result(property = "systemVersion", column = "system_version")}
    )
    List<RecordRiskInfo> queryListForLayUi(int pageIndex, int limit, RecordInfo<RiskInfo> riskInfo);

    /***
     *
     * @param riskInfo  如果查询参数只有一个就不要在sql中写入参数名称，否则报错 （多个参数用参数名称来区分）
     * @return
     */
    @Select({"<script>",
            "SELECT count(*) FROM risk_record ",
            "WHERE 1=1",
            "<if test='riskInfo.appInfo!=null and riskInfo.appInfo.appId!=null and riskInfo.appInfo.appId>0 '>AND app_id = #{riskInfo.appInfo.appId}</if>",
            "<if test='riskInfo.content!=null'>",
            "<if test='riskInfo.content.appVersionName!=null and riskInfo.content.appVersionName!=\"\" '>AND app_versionname = #{riskInfo.content.appVersionName}</if>",
            "<if test='riskInfo.content.deviceModel!=null and riskInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{riskInfo.content.deviceModel}, '%')</if>",
            "<if test='riskInfo.content.deviceBrand!=null and riskInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{riskInfo.content.deviceBrand}, '%')</if>",
            "<if test='riskInfo.content.deviceId!=null and riskInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{riskInfo.content.deviceId}, '%')</if>",
            "<if test='riskInfo.content.userId!=null and riskInfo.content.userId!=\"\" '>AND user_id=#{riskInfo.content.userId}</if>",
            "<if test='riskInfo.content.riskType!=null and riskInfo.content.riskType!=\"\" '>AND risk_type like concat('%', #{riskInfo.content.riskType}, '%')</if>",
            "<if test='riskInfo.content.startDate!=null '>AND create_time &gt; #{riskInfo.content.startDate}</if>",
            "<if test='riskInfo.content.endDate!=null  '>AND create_time &lt; #{riskInfo.content.endDate}</if>",
            "</if>",
            "</script>"})
    int queryTotalSize(RecordInfo<RiskInfo> riskInfo, int temp);

    @Select({"SELECT DISTINCT(app_versionname) FROM risk_record WHERE app_versionname!='NULL'"})
    @Cacheable(key = "#type", value = RedisUtil.GROUP_APP_VERSION_NAME, unless = "#result eq null")
    List<String> queryAppVersionNameForLayui(String type);

    @Select({"SELECT DISTINCT(risk_type) FROM risk_record WHERE risk_type!='NULL'"})
    @Cacheable(key = "#type", value = RedisUtil.GROUP_APP_VERSION_NAME, unless = "#result eq null")
    List<String> queryRiskTypeForLayui(String type);

    @Select({"<script>",
            "SELECT risk_type, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record, app_info WHERE 1=1 ",
            "AND risk_record.app_id=app_info.app_id ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY risk_type, risk_record.app_id ORDER BY total_size DESC limit #{pageIndex}, #{limit}",
            "</script>"
    })
    @Results({
            @Result(property = "riskType", column = "risk_type"),
            @Result(property = "totalSize", column = "total_size"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "minTime", column = "min_time"),
            @Result(property = "maxTime", column = "max_time"),
    })
    List<DeviceRiskOverviewEntity> queryOverviewRiskType(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM (SELECT risk_type FROM risk_record WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY risk_type, app_id) as ta",
            "</script>"
    })
    long queryOverviewRiskTypeTotalSize(@Param("recordInfo") RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({"<script>",
            "SELECT app_versioncode, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record, app_info WHERE 1=1 ",
            "AND risk_record.app_id=app_info.app_id ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY app_versioncode, risk_record.app_id ORDER BY total_size DESC limit #{pageIndex}, #{limit}",
            "</script>"
    })
    @Results({
            @Result(property = "appVersionCode", column = "app_versioncode"),
            @Result(property = "totalSize", column = "total_size"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "minTime", column = "min_time"),
            @Result(property = "maxTime", column = "max_time"),
    })
    List<DeviceRiskOverviewEntity> queryOverviewAppVersion(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM (SELECT app_versioncode FROM risk_record WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY app_versioncode, app_id) as ta",
            "</script>"
    })
    long queryOverviewAppVersionTotalSize(@Param("recordInfo") RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({"<script>",
            "SELECT device_model, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record, app_info WHERE 1=1 ",
            "AND risk_record.app_id=app_info.app_id ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY device_model, risk_record.app_id ORDER BY total_size DESC limit #{pageIndex}, #{limit}",
            "</script>"
    })
    @Results({
            @Result(property = "deviceModel", column = "device_model"),
            @Result(property = "totalSize", column = "total_size"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "minTime", column = "min_time"),
            @Result(property = "maxTime", column = "max_time"),
    })
    List<DeviceRiskOverviewEntity> queryOverviewDeviceModel(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM (SELECT device_model FROM risk_record WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY device_model, app_id) as ta",
            "</script>"
    })
    long queryOverviewDeviceModelTotalSize(@Param("recordInfo") RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({"<script>",
            "SELECT device_brand, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record, app_info WHERE 1=1 ",
            "AND risk_record.app_id=app_info.app_id ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY device_brand, risk_record.app_id ORDER BY total_size DESC limit #{pageIndex}, #{limit}",
            "</script>"
    })
    @Results({
            @Result(property = "deviceBrand", column = "device_brand"),
            @Result(property = "totalSize", column = "total_size"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "minTime", column = "min_time"),
            @Result(property = "maxTime", column = "max_time"),
    })
    List<DeviceRiskOverviewEntity> queryOverviewDeviceBrand(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM (SELECT device_brand FROM risk_record WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY device_brand, app_id) as ta",
            "</script>"
    })
    long queryOverviewDeviceBrandTotalSize(@Param("recordInfo") RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({"<script>",
            "SELECT ta.user_id, risk_user.user_phone, app_info.app_name, ta.total_size, ta.min_time, ta.max_time ",
            "FROM app_info, risk_user, ",
            "(SELECT app_id, user_id, MIN(create_time) as min_time, MAX(create_time) as max_time, COUNT(1) as total_size ",
            "FROM risk_record ",
            "WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY app_id, user_id ORDER BY total_size DESC limit #{pageIndex}, #{limit}) as ta ",
            "WHERE 1=1 ",
            "AND ta.app_id=app_info.app_id AND ta.user_id=risk_user.user_id AND ta.app_id=risk_user.app_id ORDER BY ta.total_size DESC ",
            "</script>"
    })
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userPhone", column = "user_phone"),
            @Result(property = "totalSize", column = "total_size"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "minTime", column = "min_time"),
            @Result(property = "maxTime", column = "max_time"),
    })
    List<DeviceRiskOverviewEntity> queryOverviewPhoneNum(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM (SELECT user_id, app_id FROM risk_record WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY user_id, app_id) as ta",
            "</script>"
    })
    long queryOverviewPhoneNumTotalSize(@Param("recordInfo") RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({"<script>",
            "SELECT device_id, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record, app_info WHERE 1=1 ",
            "AND risk_record.app_id=app_info.app_id ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY device_id, risk_record.app_id ORDER BY total_size DESC limit #{pageIndex}, #{limit}",
            "</script>"
    })
    @Results({
            @Result(property = "deviceId", column = "device_id"),
            @Result(property = "totalSize", column = "total_size"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "minTime", column = "min_time"),
            @Result(property = "maxTime", column = "max_time"),
    })
    List<DeviceRiskOverviewEntity> queryOverviewDeviceId(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM (SELECT device_id FROM risk_record WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY device_id, risk_record.app_id) as ta",
            "</script>"
    })
    long queryOverviewDeviceIdTotalSize(@Param("recordInfo") RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({"<script>",
            "SELECT system_version, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record, app_info WHERE 1=1 ",
            "AND risk_record.app_id=app_info.app_id ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY system_version, risk_record.app_id ORDER BY total_size DESC limit #{pageIndex}, #{limit}",
            "</script>"
    })
    @Results({
            @Result(property = "systemVersion", column = "system_version"),
            @Result(property = "totalSize", column = "total_size"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "minTime", column = "min_time"),
            @Result(property = "maxTime", column = "max_time"),
    })
    List<DeviceRiskOverviewEntity> queryOverviewAndroidVersion(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> recordInfo);

    @Select({
            "<script>",
            "SELECT COUNT(*) FROM (SELECT system_version FROM risk_record WHERE 1=1 ",
            "<if test='recordInfo.appInfo!=null and recordInfo.appInfo.appId!=null and recordInfo.appInfo.appId>0 '>AND risk_record.app_id = #{recordInfo.appInfo.appId} </if>",
            "<if test='recordInfo.content!=null'>",
            "<if test='recordInfo.content.startDate!=null '>AND create_time &gt; #{recordInfo.content.startDate} </if>",
            "<if test='recordInfo.content.endDate!=null  '>AND create_time &lt; #{recordInfo.content.endDate} </if>",
            "</if>",
            "GROUP BY system_version, risk_record.app_id) as ta",
            "</script>"
    })
    long queryOverviewAndroidVersionTotalSize(@Param("recordInfo") RecordInfo<RiskOverviewInfo> recordInfo);
}
