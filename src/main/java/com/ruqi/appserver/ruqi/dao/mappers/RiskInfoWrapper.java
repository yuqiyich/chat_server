package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.RiskInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface RiskInfoWrapper {

    @Insert("insert into risk_record(risk_type,user_id,device_id,risk_detail,create_time,device_brand,system_version,app_versionname,net_state,location_lat,location_lng,channel,app_id,app_versioncode,device_model,scene,request_ip) " +
            "values(#{riskType},#{userId},#{deviceId},#{riskDetail},#{createTime},#{deviceBrand},#{systemVersion},#{appVersionName},#{netState},#{locationLat},#{locationLng},#{channel},#{appId},#{appVersionCode},#{deviceModel},#{scene},#{requestIp})")
    int insert(RiskInfo riskInfo);
    @Select("select count(*) from risk_record where create_time > #{startTime} and create_time < #{endTime} and app_id=#{appId}")
    int countSecurityNum(int appId,Date startTime,Date endTime);
}
