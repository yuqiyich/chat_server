package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.bean.RecordRiskInfo;
import com.ruqi.appserver.ruqi.bean.RiskInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RiskInfoWrapper {

    @Insert("insert into risk_record(risk_type,user_id,device_id,risk_detail,create_time,device_brand,system_version,app_versionname,net_state,location_lat,location_lng,channel,app_id,app_versioncode,device_model,scene,request_ip) " +
            "values(#{riskType},#{userId},#{deviceId},#{riskDetail},#{createTime},#{deviceBrand},#{systemVersion},#{appVersionName},#{netState},#{locationLat},#{locationLng},#{channel},#{appId},#{appVersionCode},#{deviceModel},#{scene},#{requestIp})")
    int insert(RiskInfo riskInfo);
    @Select("select count(*) from risk_record where create_time > #{startTime} and create_time < #{endTime} and app_id=#{appId}")
    int countSecurityNum(int appId,Date startTime,Date endTime);

    @Select({"<script>",
            "SELECT * FROM",
            "(SELECT * FROM risk_record",
            "WHERE 1=1",
            "<if test='riskInfo.content!=null and riskInfo.content.appVersionName!=null and riskInfo.content.appVersionName!=\"\" '>AND app_versionname = #{riskInfo.content.appVersionName}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceModel!=null and riskInfo.content.deviceModel!=\"\" '>AND device_model = #{riskInfo.content.deviceModel}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceBrand!=null and riskInfo.content.deviceBrand!=\"\" '>AND device_brand = #{riskInfo.content.deviceBrand}</if>",
            "<if test='riskInfo.appInfo!=null and riskInfo.appInfo.appId!=null and riskInfo.appInfo.appId>0 '>AND app_id = #{riskInfo.appInfo.appId}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.startDate!=null '>AND create_time &gt; #{riskInfo.content.startDate}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.endDate!=null  '>AND create_time &lt; #{riskInfo.content.endDate}</if>",
            "order by create_time desc",
            "limit #{pageIndex}, #{limit}) as a,",
            "app_info as b,risk_user as c ",
            " where a.app_id =b.app_id and a.user_id=c.user_id",
            "<if test='riskInfo.userInfo!=null and riskInfo.userInfo.userPhone!=null and riskInfo.userInfo.userPhone!=\"\" '>AND c.user_phone = #{riskInfo.userInfo.userPhone}</if>",
            "order by create_time desc",
            "</script>"})
    @Results({@Result(property = "userInfo.userId", column = "user_id"),
              @Result(property = "userInfo.userName", column = "user_name"),
              @Result(property = "userInfo.nickName", column = "nick_name"),
              @Result(property = "userInfo.userPhone", column = "user_phone"),
              @Result(property = "appInfo.appId", column = "app_id"),
              @Result(property = "appInfo.appName", column = "app_name"),
              @Result(property = "content.appId", column = "app_id"),
              @Result(property = "content.userId", column = "user_id"),
              @Result(property = "content.deviceId", column = "device_id"),
              @Result(property = "content.riskDetail", column = "risk_detail"),
              @Result(property = "content.createTime", column = "create_time"),
              @Result(property = "content.appVersionName", column = "app_versionname"),
              @Result(property = "content.deviceBrand", column = "device_brand"),
              @Result(property = "content.appVersionCode", column = "app_versioncode"),
              @Result(property = "content.netState", column = "net_state"),
              @Result(property = "content.locationLat", column = "location_lat"),
              @Result(property = "content.locationLng", column = "location_lng"),
              @Result(property = "content.scene", column = "scene"),
              @Result(property = "content.channel", column = "channel"),
              @Result(property = "content.ext", column = "ext"),
              @Result(property = "content.systemVersion", column = "system_version")}
              )
    List<RecordInfo<RiskInfo>> queryRiskList(int pageIndex, int limit, RecordInfo<RiskInfo> riskInfo);

    @Select({"<script>",
            "SELECT * FROM",
            "(SELECT * FROM risk_record",
            "WHERE 1=1",
            "<if test='riskInfo.content!=null and riskInfo.content.appVersionName!=null and riskInfo.content.appVersionName!=\"\" '>AND app_versionname = #{riskInfo.content.appVersionName}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceModel!=null and riskInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{riskInfo.content.deviceModel}, '%')</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceBrand!=null and riskInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{riskInfo.content.deviceBrand}, '%')</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceId!=null and riskInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{riskInfo.content.deviceId}, '%')</if>",
            "<if test='riskInfo.appInfo!=null and riskInfo.appInfo.appId!=null and riskInfo.appInfo.appId>0 '>AND app_id = #{riskInfo.appInfo.appId}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.startDate!=null '>AND create_time &gt; #{riskInfo.content.startDate}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.endDate!=null  '>AND create_time &lt; #{riskInfo.content.endDate}</if>",
            "order by create_time desc) as a,",
            "app_info as b,risk_user as c ",
            " where a.app_id =b.app_id and a.user_id=c.user_id",
            "<if test='riskInfo.userInfo!=null and riskInfo.userInfo.userPhone!=null and riskInfo.userInfo.userPhone!=\"\" '>AND c.user_phone like concat('%', #{riskInfo.userInfo.userPhone}, '%')</if>",
            "order by create_time desc limit #{pageIndex}, #{limit}",
            "</script>"})
    @Results({@Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "nickName", column = "nick_name"),
            @Result(property = "userPhone", column = "user_phone"),
            @Result(property = "appId", column = "app_id"),
            @Result(property = "appName", column = "app_name"),
            @Result(property = "appId", column = "app_id"),
            @Result(property = "userId", column = "user_id"),
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
            "SELECT count(*) FROM",
            "(SELECT * FROM risk_record",
            "WHERE 1=1",
            "<if test='riskInfo.content!=null and riskInfo.content.appVersionName!=null and riskInfo.content.appVersionName!=\"\" '>AND app_versionname = #{riskInfo.content.appVersionName}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceModel!=null and riskInfo.content.deviceModel!=\"\" '>AND device_model like concat('%', #{riskInfo.content.deviceModel}, '%')</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceBrand!=null and riskInfo.content.deviceBrand!=\"\" '>AND device_brand like concat('%', #{riskInfo.content.deviceBrand}, '%')</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.deviceId!=null and riskInfo.content.deviceId!=\"\" '>AND device_id like concat('%', #{riskInfo.content.deviceId}, '%')</if>",
            "<if test='riskInfo.appInfo!=null and riskInfo.appInfo.appId!=null and riskInfo.appInfo.appId>0 '>AND app_id = #{riskInfo.appInfo.appId}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.startDate!=null '>AND create_time &gt; #{riskInfo.content.startDate}</if>",
            "<if test='riskInfo.content!=null and riskInfo.content.endDate!=null  '>AND create_time &lt; #{riskInfo.content.endDate}</if>",
            ") as a,",
            "app_info as b,risk_user as c ",
            " where a.app_id =b.app_id and a.user_id=c.user_id",
            "<if test='riskInfo.userInfo!=null and riskInfo.userInfo.userPhone!=null and riskInfo.userInfo.userPhone!=\"\" '>AND c.user_phone like concat('%', #{riskInfo.userInfo.userPhone}, '%')</if>",
            "</script>"})
    int queryTotalSize(RecordInfo<RiskInfo> riskInfo,int temp);
}
