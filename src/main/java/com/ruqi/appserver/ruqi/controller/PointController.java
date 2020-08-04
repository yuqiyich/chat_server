package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.request.EncryptBaseRequest;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.service.AppInfoSevice;
import com.ruqi.appserver.ruqi.service.IPointRecommendService;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import com.ruqi.appserver.ruqi.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Cipher;
import java.util.Map;

/**
 * 推荐上车点的控制器
 *
 * @author liangbingkun
 */
@RestController
@Api(tags = "推荐上车点的入口")
@RequestMapping(value = "/point")
public class PointController extends BaseController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IPointRecommendService pointRecommendService;
    @Autowired
    AppInfoSevice appInfoSevice;

    @ApiOperation(value = "查询推荐上车点", notes = "")
    @RequestMapping(value = "/queryRecommendPoint", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<RecommendPointList<RecommendPoint>> queryRecommendPoint(@RequestBody QueryRecommendPointRequest queryRecommendPointRequest) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(queryRecommendPointRequest));
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            result.data = pointRecommendService.queryRecommendPoint(queryRecommendPointRequest);
            return result;
        } catch (Exception e) {
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            logger.error("uploadRecommendPoint error. content:" + JsonUtil.beanToJsonStr(queryRecommendPointRequest) + ", e:" + e);
            return result;
        }
    }

    @ApiOperation(value = "推荐上车点采集上报", notes = "")
    @RequestMapping(value = "/uploadRecommendPoint", method = RequestMethod.POST)
    public BaseCodeMsgBean uploadRecommendPoint(@RequestBody UploadRecommendPointRequest<RecommendPoint> uploadRecommendPointRequest,
                                              @RequestHeader Map<String, String> header) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(uploadRecommendPointRequest));
           AppInfo appInfo = appInfoSevice.getAppInfoByKey(header.get("app_key"));

            BaseCodeMsgBean baseCodeMsgBean=new BaseCodeMsgBean();
           if (appInfo!=null){
               int appId=appInfo.getAppId();
               //硬编码指定环境
               pointRecommendService.saveRecommendPoint(uploadRecommendPointRequest,(appId==1||appId==2)?"pro":"dev");
           }
           return baseCodeMsgBean;
        } catch (Exception e) {
            BaseCodeMsgBean result = new BaseCodeMsgBean();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            logger.error("uploadRecommendPoint error. content:" + JsonUtil.beanToJsonStr(uploadRecommendPointRequest) + ", e:" + e);
            return result;
        }
    }

}
