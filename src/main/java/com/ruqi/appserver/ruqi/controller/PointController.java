package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.request.EncryptBaseRequest;
import com.ruqi.appserver.ruqi.request.QueryRecommendPointRequest;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
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
    private RedisUtil redisUtil;

    @ApiOperation(value = "查询推荐上车点", notes = "")
    @RequestMapping(value = "/queryRecommendPoint", method = RequestMethod.POST)
    @ResponseBody
    public EncryptResponse queryRecommendPoint(@RequestBody EncryptBaseRequest encryptBaseRequest) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(encryptBaseRequest));
            String sign = encryptBaseRequest.getSign();
            String aesKey = String.valueOf(redisUtil.getKey(RedisUtil.GROUP_ENCRYPT_UTIL_SIGN, sign));
            String decrypt = AESUtils.des(encryptBaseRequest.getReq(), aesKey, Cipher.DECRYPT_MODE);
            QueryRecommendPointRequest queryRecommendPointRequest = JsonUtil.jsonStrToBean(QueryRecommendPointRequest.class, decrypt);
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            result.data = pointRecommendService.queryRecommendPoint(queryRecommendPointRequest);
            String encryResponse = AESUtils.des(JsonUtil.beanToJsonStr(result), aesKey, Cipher.ENCRYPT_MODE);
            EncryptResponse encryptResponse = new EncryptResponse(true);
            encryptResponse.data = encryResponse;
            return encryptResponse;
        } catch (Exception e) {
            BaseBean<RecommendPointList<RecommendPoint>> result = new BaseBean<>();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            EncryptResponse encryptResponse = new EncryptResponse(false);
            encryptResponse.data = JsonUtil.beanToJsonStr(result);
            logger.error("uploadRecommendPoint error. content:" + JsonUtil.beanToJsonStr(encryptBaseRequest) + ", e:" + e);
            return encryptResponse;
        }
    }

    @ApiOperation(value = "推荐上车点采集上报", notes = "")
    @RequestMapping(value = "/uploadRecommendPoint", method = RequestMethod.POST)
    public EncryptResponse uploadRecommendPoint(@RequestBody EncryptBaseRequest encryptBaseRequest,
                                              @RequestHeader Map<String, String> header) {
        try {
            logger.info("queryRecommendPoint params:" + JsonUtil.beanToJsonStr(encryptBaseRequest));
            String sign = encryptBaseRequest.getSign();
            String aesKey = String.valueOf(redisUtil.getKey(RedisUtil.GROUP_ENCRYPT_UTIL_SIGN, sign));
            String decrypt = AESUtils.des(encryptBaseRequest.getReq(), aesKey, Cipher.DECRYPT_MODE);
            logger.info("queryRecommendPoint decrypt:" + decrypt);
            logger.info("queryRecommendPoint aesKey:" + aesKey);
            UploadRecommendPointRequest uploadRecommendPointRequest = JsonUtil.jsonStrToBean(UploadRecommendPointRequest.class, decrypt);
            BaseCodeMsgBean baseCodeMsgBean = pointRecommendService.saveRecommendPoint(uploadRecommendPointRequest);
            EncryptResponse encryptResponse = new EncryptResponse(true);
            encryptResponse.data = JsonUtil.beanToJsonStr(baseCodeMsgBean);
            return encryptResponse;
        } catch (Exception e) {
            BaseCodeMsgBean result = new BaseCodeMsgBean();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            EncryptResponse encryptResponse = new EncryptResponse(false);
            encryptResponse.data = JsonUtil.beanToJsonStr(result);
            logger.error("uploadRecommendPoint error. content:" + JsonUtil.beanToJsonStr(encryptBaseRequest) + ", e:" + e);
            return encryptResponse;
        }
    }

}
