package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.aspect.ApiVersion;
import com.ruqi.appserver.ruqi.aspect.LogAnnotation;
import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.response.PointList;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import com.ruqi.appserver.ruqi.request.*;
import com.ruqi.appserver.ruqi.service.AppInfoSevice;
import com.ruqi.appserver.ruqi.service.IPointRecommendService;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @ApiOperation(value = "根据扎针点查询数据库中的全部推荐上车点", notes = "给中台查询使用")
    @RequestMapping(value = "/obtainRecommendPointsForWeb", method = RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    @CrossOrigin
    public BaseBean<RecommendPointList<RecommendPoint>> obtainRecommendPointsForWeb(
            @RequestBody QueryRecommendPointForWebRequest queryRecommendPointForWebRequest) {
        try {
            logger.info("obtainRecommendPointsForWeb params:" + JsonUtil.beanToJsonStr(queryRecommendPointForWebRequest));
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            //硬编码指定环境
            if (null != queryRecommendPointForWebRequest) {
                result.data = pointRecommendService.queryRecommendPointForWeb(queryRecommendPointForWebRequest, queryRecommendPointForWebRequest.getEnv());
            }
            return result;
        } catch (Exception e) {
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            logger.error("obtainRecommendPointsForWeb error. content:" + JsonUtil.beanToJsonStr(queryRecommendPointForWebRequest) + ", e:" + e);
            return result;
        }
    }

    @ApiOperation(value = "精准查询提供推荐上车点，给客户端提供使用", notes = "")
    @RequestMapping(value = "/obtainRecommendPoint", method = RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    public BaseBean<RecommendPointList<RecommendPoint>> queryRecommendPoint(HttpServletRequest request,
                                                                            @RequestBody QueryRecommendPointRequest queryRecommendPointRequest) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(queryRecommendPointRequest));
            AppInfo appInfo = appInfoSevice.getAppInfoByKey(request.getHeader("app_key"));
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            if (appInfo != null) {
                int appId = appInfo.getAppId();
                //硬编码指定环境
                result.data = pointRecommendService.queryRecommendPoint(queryRecommendPointRequest, (appId == 1 || appId == 2) ? "pro" : "dev");
            }
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
    @LogAnnotation
    public BaseCodeMsgBean uploadRecommendPoint(HttpServletRequest request,
                                                @RequestBody UploadRecommendPointRequest<RecommendPoint> uploadRecommendPointRequest) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(uploadRecommendPointRequest));
            AppInfo appInfo = appInfoSevice.getAppInfoByKey(request.getHeader("app_key"));

            BaseCodeMsgBean baseCodeMsgBean = new BaseCodeMsgBean();
            if (appInfo != null) {
                int appId = appInfo.getAppId();
                //硬编码指定环境
                pointRecommendService.saveRecommendPoint(uploadRecommendPointRequest, (appId == 1 || appId == 2) ? "pro" : "dev");
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

    @ApiOperation(value = "查询原始点和推荐上车点数据", notes = "for web map")
    @RequestMapping(value = "/queryPoints", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public BaseBean<PointList> queryPoints(@RequestBody QueryPointsRequest queryPointsRequest) {
        try {
            logger.info("queryPoints params:" + JsonUtil.beanToJsonStr(queryPointsRequest));
            if (null == queryPointsRequest) {
                queryPointsRequest = new QueryPointsRequest();
            }
            BaseBean<PointList> result = new BaseBean<>();
            PointList pointList = new PointList();
            pointList.areaType = queryPointsRequest.getAreaType();
            pointList.points = pointRecommendService.queryPoints(queryPointsRequest);
            result.data = pointList;
            return result;
        } catch (Exception e) {
            BaseBean result = new BaseBean<>();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = MyStringUtils.isEmpty(e.getMessage()) ?
                    ErrorCode.ERROR_SYSTEM.errorMsg : e.getMessage();
            logger.error("queryPoints error params:" + JsonUtil.beanToJsonStr(queryPointsRequest) + ". e:" + e);
            return result;
        }
    }

    @ApiOperation(value = "查询推荐上车点中台统计数据", notes = "for web")
    @RequestMapping(value = "/{apiVersion}/queryStaticsRecommendPoints", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    @ApiVersion()
    public BaseBean<RecommendPointList<RecommentPointStaticsInfo>> queryStaticsRecommendPoints(
            @Validated @RequestBody QueryStaticRecommendPointsRequest queryStaticRecommendPointsRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            BaseBean result = new BaseBean<>();
            result.errorCode = ErrorCodeMsg.ERROR_INVALID_PARAMS.errorCode;
            result.errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return result;
        } else {
            try {
                logger.info("queryStaticRecommendPointsRequest params:" + JsonUtil.beanToJsonStr(queryStaticRecommendPointsRequest));
                if (null == queryStaticRecommendPointsRequest) {
                    queryStaticRecommendPointsRequest = new QueryStaticRecommendPointsRequest();
                }
                BaseBean<RecommendPointList<RecommentPointStaticsInfo>> result = new BaseBean<>();
                result.data = pointRecommendService.queryStaticsRecommendPoint(queryStaticRecommendPointsRequest);
                return result;
            } catch (Exception e) {
                BaseBean result = new BaseBean<>();
                result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
                result.errorMsg = MyStringUtils.isEmpty(e.getMessage()) ?
                        ErrorCode.ERROR_SYSTEM.errorMsg : e.getMessage();
                logger.error("queryStaticRecommendPointsRequest error params:" + JsonUtil.beanToJsonStr(queryStaticRecommendPointsRequest) + ". e:" + e);
                return result;
            }
        }
    }

    @ApiOperation(value = "查询推荐上车点中台统计数据V2", notes = "for web")
    @RequestMapping(value = "/{apiVersion}/queryStaticsRecommendPoints", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    @ApiVersion(2)
    public BaseBean<RecommendPointList<RecommentPointStaticsInfo>> queryStaticsRecommendPointsV2(
            @Validated @RequestBody QueryStaticRecommendPointsRequest queryStaticRecommendPointsRequest,
            BindingResult bindingResult) {
        logger.error("queryStaticsRecommendPointsV2 params:" + (null == queryStaticRecommendPointsRequest
                ? "" : JsonUtil.beanToJsonStr(queryStaticRecommendPointsRequest)));
        if (bindingResult.hasErrors()) {
            BaseBean result = new BaseBean<>();
            result.errorCode = ErrorCodeMsg.ERROR_INVALID_PARAMS.errorCode;
            result.errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return result;
        } else {
            BaseBean result = new BaseBean<>();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = ErrorCode.ERROR_SYSTEM.errorMsg;
            return result;
        }
    }

}
