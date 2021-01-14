package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.AreaAdInfo;
import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.SignResponse;
import com.ruqi.appserver.ruqi.dao.mappers.BaseAdAreaInfoWrapper;
import com.ruqi.appserver.ruqi.geomesa.mapdatasnyc.MapRegionSnycTool;
import com.ruqi.appserver.ruqi.request.SignRequest;
import com.ruqi.appserver.ruqi.service.BaseConfigAreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.yarn.webapp.Controller;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Api(tags = "工具类")
@RequestMapping(value = "/tool")
public class ToolController {
    @Autowired
    private BaseConfigAreaService adAreaInfoWrapper;

    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/syncAreaAdInfo", method = RequestMethod.GET)
    public BaseBean<SignResponse> syncAreaAdInfo() {
        MapRegionSnycTool.District data= MapRegionSnycTool.storeChinaAdCodeInfo();
        saveAreaInfo(data);
        return new BaseBean<SignResponse>();
    }

    @ApiOperation(value = "", notes = "")
    @RequestMapping(value = "/getAreaAdInfo", method = RequestMethod.GET)
    public AreaAdInfo getAreaAdInfo(@Param("adCode") String adCode) {
       return adAreaInfoWrapper.getAreaAdInfo(adCode);
    }

    private  void saveAreaInfo(MapRegionSnycTool.District data) {
        if (data.districts!=null){
            for (MapRegionSnycTool.District item:data.districts){
                AreaAdInfo areaAdInfo=convertDistrictToAreaAdInfo(item);
                if (areaAdInfo!=null)
                    adAreaInfoWrapper.saveAreaAdInfo(areaAdInfo);
                saveAreaInfo(item);
            }
        }
    }

    private  AreaAdInfo convertDistrictToAreaAdInfo(MapRegionSnycTool.District data) {
        AreaAdInfo adInfo=null;
        if (data!=null&& !StringUtils.isEmpty(data.adcode)){
            adInfo= new AreaAdInfo();
            adInfo.setAdCode(data.adcode);
            adInfo.setAdName(data.name);
            adInfo.setLevel(data.level);
            adInfo.setCenterLat(Double.valueOf(data.center.split(",")[1]));
            adInfo.setCenterLng(Double.valueOf(data.center.split(",")[0]));
        }
        return  adInfo;
    }

}
