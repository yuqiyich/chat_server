package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.bean.AppResponeInfo;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yich
 */
@Repository
public interface AppInfoWrapper {
    @Select("SELECT app_id FROM app_info where app_key = #{key}")
    int getAppIdByKey(String key);

    @Select("SELECT * FROM app_info where app_key = #{key}")
    @Results({@Result(property = "appId", column = "app_id"), @Result(property = "appName", column = "app_name")})
    AppInfo getAppInfoByKey(String key);


    /**
     * 后面优化用redis来缓存所有的app的信息
     *
     * @return
     */
    @Select("SELECT * FROM app_info ")
    @Results({@Result(property = "appId", column = "app_id"), @Result(property = "appName", column = "app_name")})
    List<AppResponeInfo> getAllApps();
}
