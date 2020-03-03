package com.ruqi.appserver.ruqi.dao.mappers;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author yich
 */
@Repository
public interface AppInfoWrapper {
    @Select("SELECT app_id FROM app_info where app_key = #{key}")
   int getAppIdByKey(String key);
}
