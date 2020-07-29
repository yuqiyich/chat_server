package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.*;
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
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvG9mmgvBXtPa0QH6RfA87MkCBeedWwLIv+HylZqtrZyKLS4fN/HehsfESEBrmea+MQWSOT6WJIxnhLze+jC9im56A8IloSzxAtCTSvDCzT290RLcduvy59OBSlV6PbWWI60yl5sacBnfn7wfvdt/Qjwb8pT7BiwaEDEPA/69kkvHMgCq5XJSpmbMoh/lllzuFI1EUYSwHl0doFoeFOj51tQlxNnYgaOP8+jEye3MO94SDOcnRIB5KxL8J3WHk/ol7cLlqXTdNrH0f1ED566IUqmZ73E972H38GW1kJPpGHuPi/KFVBAishiqPoJo5JDsi3OtGJa8tmQtb2qy6DEsEQIDAQAB";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8b2aaC8Fe09rRAfpF8DzsyQIF551bAsi/4fKVmq2tnIotLh838d6Gx8RIQGuZ5r4xBZI5PpYkjGeEvN76ML2KbnoDwiWhLPEC0JNK8MLNPb3REtx26/Ln04FKVXo9tZYjrTKXmxpwGd+fvB+9239CPBvylPsGLBoQMQ8D/r2SS8cyAKrlclKmZsyiH+WWXO4UjURRhLAeXR2gWh4U6PnW1CXE2diBo4/z6MTJ7cw73hIM5ydEgHkrEvwndYeT+iXtwuWpdN02sfR/UQPnrohSqZnvcT3vYffwZbWQk+kYe4+L8oVUECKyGKo+gmjkkOyLc60Ylry2ZC1varLoMSwRAgMBAAECggEAEZtJF3CsvzsFUY0hQOKrFqbLcRjMm6HddwxaGb9rpfKf+Iu8MAKB+87zmJqoUyd7suHl4UJnTf/udjkdjnv6YdJjxTEhSfeEh3JMqO5pDTtNHploDxaJyj55cKQe+WAbqLa4qPC9nibUvbyarKczM1GhfN+Nuuxo/QVQdVuag+42nM/S8cjJJh6jo69iVS2H7LprflQbNArGWN9qHNXwKM3mWjADF4xfV4LNi/OsXX9vjF1b7yiH/a9kv1vosq0GS2DWtNvd1+TY5sKKV9CZBoBRd0xA9PqRKQ4MAabQhQ5OBbmQ4/sWYaeBBoKUn3U93hfc+P8je1bQveVQVFQxVwKBgQDidfsRRn5MCyKuCGlYenTwQH3OmjC6qbhIUku9WzdNUEaxJr0iKbiXhjZjB/vy9g6GHnzTJ4xFKuQEyyWQG+zLmyfC0DRf+U9bATqVwi5T/If0XQ5wqo5MjHPtDR39K/pLW+wiZrs4po+gq6o0osV56UUBRMB8t0/sqcfsDcW4qwKBgQDVA6jM9E/QY0vn0iEn+jH7764KzrGFCF3p6L+4XIw8mbKCmR862HPOexxLnE9bGDVibReE27zdzutzvoeGUSnC56Ouk9RpbwWhmTOLP+djgrQNpLVxI6cT0SzfqnPNnqtR8MoY3ZJaqSY4lYQFmYfVaFAdBgVDDJdKgQeVOSwmMwKBgEImxJhfRzVfa1n7CwrVeqNTs2xOjj14pmQ55fYCVz02XfARqN354fohMnHrOyXVyphS/5OO0eLCjKj5zpcyERHI2OyHdUUzxoKG8V4dwvq1oeE37afrqnWh8ZslYcU6u3qX93p1F+uMfBgrDSUjBxx9j7K3KqNDyQ0Q612BCGjRAoGANbbchAI/dh7z7xsvvTL8E7mWu6bvYMqBVBCa99RukIF5YDFYjLA0U6b7tZ1O7XunSpCT067Na2lYOjGbXyVsUHe08LraX1PdqahGNSECKje1S5NzJXqGERs4I5aJ6RnPvbPoYmjNFQt+VdpuFjNm60uImCkCfqvYIrNyxBBMr70CgYEAhHBqi/r6JZJQ+T3oMNr+OKE/czxCBrrFI5kRStHLMZ2UxkgpFQd5Dw1WIlBdmm1tO6W8VRVEsh2aWx7obissp8cwukTrpP73aS721sXt0NR2kl8GIgD8MVtG8TuMxgKH8kBgdyUNLD8FHTyYsmwJDHY6jp9aOn/0J2GEn7jwKNU=";

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "申请sign", notes = "")
    @RequestMapping(value = "/obtain", method = RequestMethod.POST)
    public BaseBean<SignResponse> obtainSign(@RequestBody SignRequest signRequest,
                                              @RequestHeader Map<String, String> header) {
        try {
            logger.info("obtainSign params:" + JsonUtil.beanToJsonStr(signRequest));
            String aesKey =  new String(RSAUtil.privateDecrypt(Base64Util.base642Byte(signRequest.getAesKey()), RSAUtil.string2PrivateKey(PRIVATE_KEY)));
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
