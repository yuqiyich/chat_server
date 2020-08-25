package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecommendPointWrapper {

    @Insert("replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date, env) " +
            "values(#{recommentPointStaticsInfo.cityCode},#{recommentPointStaticsInfo.cityName},#{recommentPointStaticsInfo.totalOriginPointNum},#{recommentPointStaticsInfo.totalRecmdPointNum},#{recommentPointStaticsInfo.totalRecordNum},#{recommentPointStaticsInfo.staticsDate},#{recommentPointStaticsInfo.env})")
    int insertRecommendPoint(@Param("recommentPointStaticsInfo") RecommentPointStaticsInfo recommentPointStaticsInfo);

    @Select("SELECT * FROM recommend_point_statics where city_code = #{cityCode} and env = #{env} and date_sub(curdate(), INTERVAL 7 DAY) <= date(`date`) and date(`date`) < curdate() GROUP BY `date`")
    @Results({@Result(property = "cityCode", column = "city_code"),
            @Result(property = "cityName", column = "city_name"),
            @Result(property = "totalOriginPointNum", column = "total_record_num"),
            @Result(property = "totalRecmdPointNum", column = "total_recmd_point_num"),
            @Result(property = "totalRecordNum", column = "total_origin_point_num"),
            @Result(property = "staticsDate", column = "date"),
            @Result(property = "env", column = "env")})
    List<RecommentPointStaticsInfo> getRecommendPointLastWeek(@Param("env") String env, @Param("cityCode") String cityCode);
}
