package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.RiskInfo;
import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskInfoWrapper {

    @Insert("insert into risk_record(risk_type,user_id,device_id,risk_detail,create_time,device_brand,system_version,app_version,net_state,location_lat,location_lng,channel,app_id) " +
            "values(#{riskType},#{userId},#{deviceId},#{riskDetail},#{createTime},#{deviceBrand},#{systemVersion},#{appVersion},#{netState},#{locationLat},#{locationLng},#{channel},#{appId})")
    int insert(RiskInfo riskInfo);
}
