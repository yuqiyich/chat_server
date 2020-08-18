package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.config.RuqiServiceConfig;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.request.SignRequest;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import com.ruqi.appserver.ruqi.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.ruqi.appserver.ruqi.request.HeaderParams.KEY_DEVICE_ID;

/**
 * 申请sign的控制器
 *
 * @author liangbingkun
 */
@RestController
@Api(tags = "sign申请入口")
@RequestMapping(value = "/sign")
public class SignController extends BaseController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RuqiServiceConfig ruqiServiceConfig;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "申请sign", notes = "")
    @RequestMapping(value = "/obtain", method = RequestMethod.POST)
    public BaseBean<SignResponse> obtainSign(@RequestBody SignRequest signRequest,
                                              @RequestHeader Map<String, String> header) {
        try {
            logger.info("obtainSign params:" + JsonUtil.beanToJsonStr(signRequest));
            String aesKey =  new String(RSAUtil.privateDecrypt(Base64Util.base642Byte(signRequest.getAesKey()), RSAUtil.string2PrivateKey(ruqiServiceConfig.getPrivateKey())));
            String deviceId =  header.get(KEY_DEVICE_ID);
            String sign = SHA1Util.encode(deviceId + System.currentTimeMillis());
            logger.info("obtainSign aesKey:" + aesKey + ",deviceId:" + deviceId + ",sign:" + sign);
            redisUtil.putKey(RedisUtil.GROUP_ENCRYPT_UTIL_SIGN, sign,
                    aesKey, RedisUtil.EXPIRE_WEEK, TimeUnit.SECONDS);
            BaseBean<SignResponse> responseBaseBean = new BaseBean<SignResponse>();
            SignResponse signResponse = new SignResponse();
            signResponse.setSign(sign);
            responseBaseBean.data = signResponse;
            return responseBaseBean;
        } catch (Exception e) {
            BaseBean result = new BaseBean();
            result.errorCode = ErrorCode.ERROR_SYSTEM.errorCode;
            result.errorMsg = e.getMessage();
            logger.error("obtainSign error. content:" + signRequest.getAesKey() + ", e:" + e);
            return result;
        }
    }
}
