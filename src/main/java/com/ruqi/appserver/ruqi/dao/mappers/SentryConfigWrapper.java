package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentryConfigWrapper {

    @Select("SELECT * FROM sentry_area where areacode = #{areacode} or citycode = #{citycode}")
    @Results({@Result(property = "citycode", column = "citycode"),
            @Result(property = "areacode", column = "areacode")})
    List<SentryAreaEntity> getSentryArea(@Param("areacode") String areacode, @Param("citycode") String citycode);

    @Select("SELECT * FROM sentry_platform where platform = #{platform} and environment = #{environment}")
    @Results({@Result(property = "platform", column = "platform"),
            @Result(property = "environment", column = "environment")})
    List<SentryPlatformEntity> getSentryPlatform(@Param("platform") String platform, @Param("environment") String environment);

    @Select("SELECT * FROM sentry_monitoring_object where mobile = #{mobile}")
    @Results({@Result(property = "mobile", column = "mobile"),
            @Result(property = "name", column = "name")})
    List<SentryMonitoringObjectEntity> getSentryMonitoringObject(@Param("mobile") String mobile);

    @Select("SELECT * FROM sentry_switch")
    @Results({@Result(property = "sentrySwitch", column = "switch"),
            @Result(property = "level", column = "level")})
    List<SentryConfigEntity> getSentryConfig();
}
