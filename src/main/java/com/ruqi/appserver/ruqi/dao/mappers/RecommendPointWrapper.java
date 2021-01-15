package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.RecommentPointStaticsInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendPointWrapper {

    @Insert({"<script>",
            "replace into recommend_point_statics(ad_code, ad_name, total_record_num, total_recmd_point_num,",
            " total_origin_point_num,date, env, center_lng, center_lat)",
            " values ",
            " <foreach collection='recommentPointStaticsInfoList' item='recommentPointStaticsInfo' separator=','>",
            "(#{recommentPointStaticsInfo.adCode}, #{recommentPointStaticsInfo.adName},",
            " #{recommentPointStaticsInfo.totalRecordNum}, #{recommentPointStaticsInfo.totalRecmdPointNum},",
            " #{recommentPointStaticsInfo.totalOriginPointNum}, #{recommentPointStaticsInfo.staticsDate},",
            " #{recommentPointStaticsInfo.env}, #{recommentPointStaticsInfo.centerLng}, #{recommentPointStaticsInfo.centerLat})",
            "</foreach> ",
            "</script>"})
    int insertRecommendPoint(@Param("recommentPointStaticsInfoList") List<RecommentPointStaticsInfo> recommentPointStaticsInfoList);

    @Select({"<script>",
            "SELECT * FROM recommend_point_statics where env = #{env}",
            "<if test='adCode!=null and adCode!=\"\"'> AND ad_code = #{adCode}</if>",
            " AND date >= date_sub(curdate(), INTERVAL 7 DAY)",
            "</script>"})
    @Results({@Result(property = "adCode", column = "ad_code"),
            @Result(property = "adName", column = "ad_name"),
            @Result(property = "totalRecordNum", column = "total_record_num"),
            @Result(property = "totalRecmdPointNum", column = "total_recmd_point_num"),
            @Result(property = "totalOriginPointNum", column = "total_origin_point_num"),
            @Result(property = "staticsDate", column = "date"),
            @Result(property = "env", column = "env")})
    List<RecommentPointStaticsInfo> getRecommendPointLastWeek(@Param("env") String env, @Param("adCode") String adCode);

    @Select({"<script>",
            "SELECT * FROM recommend_point_statics where env = #{env} and date = DATE_SUB(curdate(),INTERVAL 1 DAY) ",
            "<if test='adCodeWhereCause!=null and adCodeWhereCause!=\"\"'> AND ${adCodeWhereCause}</if>",
            "</script>"})
    @Results({@Result(property = "adCode", column = "ad_code"),
            @Result(property = "adName", column = "ad_name"),
            @Result(property = "totalRecordNum", column = "total_record_num"),
            @Result(property = "totalRecmdPointNum", column = "total_recmd_point_num"),
            @Result(property = "totalOriginPointNum", column = "total_origin_point_num"),
            @Result(property = "staticsDate", column = "date"),
            @Result(property = "centerLng", column = "center_lng"),
            @Result(property = "centerLat", column = "center_lat"),
            @Result(property = "env", column = "env")})
    List<RecommentPointStaticsInfo> getRecmdPointStaticBeforeToday(@Param("env") String env, @Param("adCodeWhereCause") String cause);
}
