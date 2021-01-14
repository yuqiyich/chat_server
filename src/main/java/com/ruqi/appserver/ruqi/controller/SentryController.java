package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.aspect.LogAnnotation;
import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.request.*;
import com.ruqi.appserver.ruqi.service.*;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * sentry 配置控制
 *
 * @author liangbingkun
 */
@RestController
@Api(tags = "sentry 配置控制")
@RequestMapping(value = "/sentry")
public class SentryController extends BaseController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    ISentryConfigService sentryConfigService;

    @ApiOperation(value = "查询sentry配置", notes = "")
    @RequestMapping(value = "/querySentryConfig", method = RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    public BaseBean<SentryConfigEntity> querySentryConfig(HttpServletRequest request,
                                                                            @RequestBody SentryConfigRequest sentryConfigRequest) {
        try {
            logger.info("querySentryConfig params:" + JsonUtil.beanToJsonStr(sentryConfigRequest));
            BaseBean<SentryConfigEntity> result = new BaseBean<>();
            //硬编码指定环境
            result.data = sentryConfigService.querySentryConfig(sentryConfigRequest);
            return result;
        } catch (Exception e) {
            BaseBean<SentryConfigEntity> result = new BaseBean<>();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            logger.error("querySentryConfig error. content:" + JsonUtil.beanToJsonStr(sentryConfigRequest) + ", e:" + e);
            return result;
        }
    }
}
