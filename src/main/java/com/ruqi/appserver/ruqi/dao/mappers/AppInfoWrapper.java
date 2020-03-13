package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author yich
 */
@Repository
public interface AppInfoWrapper {
    @Select("SELECT app_id FROM app_info where app_key = #{key}")
   int getAppIdByKey(String key);

    @Select("SELECT * FROM app_info where app_key = #{key}")
    @Results({@Result(property = "appId", column = "app_id"),@Result(property = "appName", column = "app_name")})
    AppInfo getAppInfoByKey(String key);
}
