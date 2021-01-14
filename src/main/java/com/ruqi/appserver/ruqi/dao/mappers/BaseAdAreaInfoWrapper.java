package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.bean.AppResponeInfo;
import com.ruqi.appserver.ruqi.bean.AreaAdInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yich
 */
@Repository
public interface BaseAdAreaInfoWrapper {


    @Select("SELECT * FROM base_config_ad_area_info where ad_code = #{adCode}")
    @Results({@Result(property = "adCode", column = "ad_code"),
            @Result(property = "adName", column = "ad_name"),
            @Result(property = "centerLat", column = "center_lat"),
            @Result(property = "centerLng", column = "center_lng"),
            @Result(property = "level", column = "level")
    })
    AreaAdInfo getAreaAdInfo(String adCode);

    @Insert(" insert into base_config_ad_area_info(ad_code,ad_name,center_lat,center_lng,level)  values(#{areaAdInfo.adCode},#{areaAdInfo.adName},#{areaAdInfo.centerLat},#{areaAdInfo.centerLng},#{areaAdInfo.level})" +
            " ON DUPLICATE KEY UPDATE ad_name= #{areaAdInfo.adName},center_lat=#{areaAdInfo.centerLat} ,center_lng=#{areaAdInfo.centerLng},level =#{areaAdInfo.level} ")
    void saveAreaAdInfo(@Param("areaAdInfo") AreaAdInfo areaAdInfo);
}
