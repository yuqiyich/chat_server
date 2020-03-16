package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.RecordInfo;
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
            "<if test='riskInfo.content.appVersionName!=null'>AND app_versionname = #{riskInfo.content.appVersionName}</if>",
            "<if test='riskInfo.content.deviceModel!=null'>AND device_model = #{riskInfo.content.deviceModel}</if>",
            "<if test='riskInfo.content.deviceBrand!=null'>AND device_brand = #{riskInfo.content.deviceBrand}</if>",
            "limit #{pageIndex}, #{limit}) as a,",
            "app_info as b,risk_user as c ",
            " where a.app_id =b.app_id and a.user_id=c.user_id",
            "</script>"})
    @Results({@Result(property = "userInfo.userId", column = "user_id"),
              @Result(property = "userInfo.userName", column = "user_name"),
              @Result(property = "userInfo.userPhone", column = "user_phone"),
              @Result(property = "appInfo.appId", column = "app_id"),
              @Result(property = "appInfo.appName", column = "app_name"),
              @Result(property = "content.appId", column = "app_id"),
              @Result(property = "content.userName", column = "user_name"),
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

}
