package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentryConfigWrapper {

    @Deprecated
    @Select("SELECT * FROM sentry_area where areacode = #{areacode} or citycode = #{citycode}")
    @Results({@Result(property = "citycode", column = "citycode"),
            @Result(property = "areacode", column = "areacode")})
    List<SentryAreaEntity> getSentryArea(@Param("areacode") String areacode, @Param("citycode") String citycode);


    @Select("SELECT * FROM sentry_area where( areacode = #{areacode} or citycode = #{citycode}) and project_id=#{project_id}")
    @Results({@Result(property = "citycode", column = "citycode"),
            @Result(property = "areacode", column = "areacode")})
    List<SentryAreaEntity> getSentryAreaByProject(@Param("areacode") String areacode, @Param("citycode") String citycode, @Param("project_id") int project_id);
    /**
     * 老版本的sentry才会配置
     * 新版本不会调用
     *
     * @param platform
     * @param environment
     * @return
     */
    @Deprecated
    @Select("SELECT * FROM sentry_platform where platform = #{platform} and environment = #{environment}")
    @Results({@Result(property = "platform", column = "platform"),
            @Result(property = "environment", column = "environment")})
    List<SentryPlatformEntity> getSentryPlatform(@Param("platform") String platform, @Param("environment") String environment);

    @Deprecated
    @Select("SELECT * FROM sentry_monitoring_object where mobile = #{mobile}")
    @Results({@Result(property = "mobile", column = "mobile"),
            @Result(property = "name", column = "name")})
    List<SentryMonitoringObjectEntity> getSentryMonitoringObject(@Param("mobile") String mobile);

    @Select("SELECT * FROM sentry_monitoring_object where mobile = #{mobile} and  project_id = #{project_id}")
    @Results({@Result(property = "mobile", column = "mobile"),
            @Result(property = "name", column = "name")})
    List<SentryMonitoringObjectEntity> getSentryMonitoringObjectByProject(@Param("mobile") String mobile, @Param("project_id") int project_id);

    /**
     * 老版本的sentry配置
     *
     * @return
     */
    @Deprecated
    @Select("SELECT * FROM sentry_switch")
    @Results({@Result(property = "sentrySwitch", column = "switch"),
            @Result(property = "dns", column = "dns"),
            @Result(property = "level", column = "level")})
    List<SentryConfigEntity> getSentryConfig();

    @Select("SELECT * FROM sentry_switch where  project= #{project}  and platform= #{platform} and env= #{env}")
    @Results({@Result(property = "sentrySwitch", column = "switch"),
            @Result(property = "dns", column = "dns"),
            @Result(property = "id", column = "id"),
            @Result(property = "areaSwitch", column = "area_switch"),
            @Result(property = "sampleRate", column = "sample_rate"),
            @Result(property = "tracesSampleRate", column = "traces_sample_rate"),
            @Result(property = "isHandleVisit", column = "is_handle_visit"),
            @Result(property = "visitSampleRate", column = "visit_sample_rate"),
            @Result(property = "isHandleApi", column = "is_handle_api"),
            @Result(property = "apiSampleRate", column = "api_sample_rate"),
            @Result(property = "isHandleResource", column = "is_handle_resource"),
            @Result(property = "resourceSampleRate", column = "resource_sample_rate"),
            @Result(property = "level", column = "level")})
    List<SentryConfigEntity> getSentryConfigByProject(@Param("project") String project, @Param("platform") String platform,@Param("env") String env);

    @Deprecated
    @Select("SELECT  DISTINCT(tag) FROM sentry_tags where  platform= #{platform}")
    List<String> getSentryTags(@Param("platform") String platform);

    @Select("SELECT  DISTINCT(tag) FROM sentry_tags where  project_id= #{project_id}")
    List<String> getSentryTagsByProject(@Param("project_id") int projectId);


    @Select("SELECT  loop_max_count FROM sentry_api_static_config where  project_id= #{project_id}")
    int getSentryHttpConfigByProject(@Param("project_id") int projectId);
}
