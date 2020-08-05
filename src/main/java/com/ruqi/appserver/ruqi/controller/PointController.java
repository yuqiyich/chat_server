package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.BaseCodeMsgBean;
import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.bean.RecommendPointList;
import com.ruqi.appserver.ruqi.kafka.BaseKafkaLogInfo;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.service.IPointRecommendService;
import com.ruqi.appserver.ruqi.utils.HeaderMapUtils;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @ApiOperation(value = "查询推荐上车点", notes = "")
    @RequestMapping(value = "/queryRecommendPoint", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<RecommendPointList<RecommendPoint>> queryRecommendPoint(HttpServletRequest request,
                                                                            @RequestBody QueryRecommendPointRequest queryRecommendPointRequest) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(queryRecommendPointRequest));
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            result.data = pointRecommendService.queryRecommendPoint(queryRecommendPointRequest);
            kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.INFO,
                    String.format("request:[%s], head:[%s], body:[%s], response:[%s]", JsonUtil.beanToJsonStr(request.getRequestURL()),
                            JsonUtil.beanToJsonStr(HeaderMapUtils.getAllHeaderParamMaps(request)),
                            null, JsonUtil.beanToJsonStr(result)));
            return result;
        } catch (Exception e) {
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            logger.error("uploadRecommendPoint error. content:" + JsonUtil.beanToJsonStr(queryRecommendPointRequest) + ", e:" + e);
            kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.ERROR,
                    String.format("request:[%s], head:[%s], body:[%s], response:[%s]", JsonUtil.beanToJsonStr(request.getRequestURL()),
                            JsonUtil.beanToJsonStr(HeaderMapUtils.getAllHeaderParamMaps(request)),
                            null, JsonUtil.beanToJsonStr(result)));
            return result;
        }
    }

    @ApiOperation(value = "推荐上车点采集上报", notes = "")
    @RequestMapping(value = "/uploadRecommendPoint", method = RequestMethod.POST)
    public BaseCodeMsgBean uploadRecommendPoint(HttpServletRequest request,
                                                @RequestBody UploadRecommendPointRequest<RecommendPoint> uploadRecommendPointRequest,
                                                @RequestHeader Map<String, String> header) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(uploadRecommendPointRequest));
            BaseCodeMsgBean baseCodeMsgBean = pointRecommendService.saveRecommendPoint(uploadRecommendPointRequest);
            kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.INFO,
                    String.format("request:[%s], head:[%s], body:[%s], response:[%s]", JsonUtil.beanToJsonStr(request.getRequestURL()),
                            JsonUtil.beanToJsonStr(HeaderMapUtils.getAllHeaderParamMaps(request)),
                            null, JsonUtil.beanToJsonStr(baseCodeMsgBean)));
            return baseCodeMsgBean;
        } catch (Exception e) {
            BaseCodeMsgBean result = new BaseCodeMsgBean();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            logger.error("uploadRecommendPoint error. content:" + JsonUtil.beanToJsonStr(uploadRecommendPointRequest) + ", e:" + e);
            kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.ERROR,
                    String.format("request:[%s], head:[%s], body:[%s], response:[%s]", JsonUtil.beanToJsonStr(request.getRequestURL()),
                            JsonUtil.beanToJsonStr(HeaderMapUtils.getAllHeaderParamMaps(request)),
                            null, JsonUtil.beanToJsonStr(result)));
            return result;
        }
    }

}
