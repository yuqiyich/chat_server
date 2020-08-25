package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.AppResponeInfo;
import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配置类
 */
@RestController
@Api(tags = "配置信息")
@RequestMapping(value = "/config")
@CrossOrigin
public class ConfigController extends BaseController {
//    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AppInfoWrapper appInfoWrapper;

    @ApiOperation(value = "查询应用列表", notes = "")
    @RequestMapping(value = "/queryApps", method = RequestMethod.GET)
    @ResponseBody
    public BaseBean<List<AppResponeInfo>> queryRiskTypeForLayui() {
        BaseBean<List<AppResponeInfo>> result = new BaseBean<>();
        List<AppResponeInfo> appInfos = appInfoWrapper.getAllApps();
        result.data = appInfos;
        return result;
    }

}