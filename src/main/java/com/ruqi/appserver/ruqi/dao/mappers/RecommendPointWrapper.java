package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.RecommentPointStaticsInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendPointWrapper {

    @Insert("replace into recommend_point_statics(ad_code,ad_name,total_record_num,total_recmd_point_num," +
            "total_origin_point_num,date, env) " +
            "values(#{recommentPointStaticsInfo.cityCode},#{recommentPointStaticsInfo.cityName}," +
            "#{recommentPointStaticsInfo.totalRecordNum},#{recommentPointStaticsInfo.totalRecmdPointNum}," +
            "#{recommentPointStaticsInfo.totalOriginPointNum},#{recommentPointStaticsInfo.staticsDate},#{recommentPointStaticsInfo.env})")
    int insertRecommendPoint(@Param("recommentPointStaticsInfo") RecommentPointStaticsInfo recommentPointStaticsInfo);

    @Select("SELECT * FROM recommend_point_statics where ad_code = #{cityCode} and env = #{env} and date_sub(curdate(), " +
            "INTERVAL 7 DAY) <= date(`date`) and date(`date`) < curdate() GROUP BY `date`")
    @Results({@Result(property = "cityCode", column = "ad_code"),
            @Result(property = "cityName", column = "ad_name"),
            @Result(property = "totalRecordNum", column = "total_record_num"),
            @Result(property = "totalRecmdPointNum", column = "total_recmd_point_num"),
            @Result(property = "totalOriginPointNum", column = "total_origin_point_num"),
            @Result(property = "staticsDate", column = "date"),
            @Result(property = "env", column = "env")})
    List<RecommentPointStaticsInfo> getRecommendPointLastWeek(@Param("env") String env, @Param("cityCode") String cityCode);

    @Select("SELECT * FROM recommend_point_statics where env = #{env} and date_sub(curdate(), INTERVAL 7 DAY) <= date(`date`) and date(`date`) < curdate()")
    @Results({@Result(property = "cityCode", column = "ad_code"),
            @Result(property = "cityName", column = "ad_name"),
            @Result(property = "totalRecordNum", column = "total_record_num"),
            @Result(property = "totalRecmdPointNum", column = "total_recmd_point_num"),
            @Result(property = "totalOriginPointNum", column = "total_origin_point_num"),
            @Result(property = "staticsDate", column = "date"),
            @Result(property = "env", column = "env")})
    List<RecommentPointStaticsInfo> getRecmdPointLastWeek(@Param("env") String env);

    @Select("SELECT * FROM recommend_point_statics where #{adCodeWhereCause} and env = #{env} and   date(`date`)= DATE_SUB(curdate(),INTERVAL 1 DAY) ")
    @Results({@Result(property = "cityCode", column = "ad_code"),
            @Result(property = "cityName", column = "ad_name"),
            @Result(property = "totalRecordNum", column = "total_record_num"),
            @Result(property = "totalRecmdPointNum", column = "total_recmd_point_num"),
            @Result(property = "totalOriginPointNum", column = "total_origin_point_num"),
            @Result(property = "staticsDate", column = "date"),
            @Result(property = "env", column = "env")})
    List<RecommentPointStaticsInfo> getRecmdPointStaticBeforeToday(@Param("env") String env,@Param("adCodeWhereCause") String cause);
}
