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
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvG9mmgvBXtPa0QH6RfA87MkCBeedWwLIv+HylZqtrZyKLS4fN/HehsfESEBrmea+MQWSOT6WJIxnhLze+jC9im56A8IloSzxAtCTSvDCzT290RLcduvy59OBSlV6PbWWI60yl5sacBnfn7wfvdt/Qjwb8pT7BiwaEDEPA/69kkvHMgCq5XJSpmbMoh/lllzuFI1EUYSwHl0doFoeFOj51tQlxNnYgaOP8+jEye3MO94SDOcnRIB5KxL8J3WHk/ol7cLlqXTdNrH0f1ED566IUqmZ73E972H38GW1kJPpGHuPi/KFVBAishiqPoJo5JDsi3OtGJa8tmQtb2qy6DEsEQIDAQAB";
    private static final String PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC8b2aaC8Fe09rRAfpF8DzsyQIF551bAsi/4fKVmq2tnIotLh838d6Gx8RIQGuZ5r4xBZI5PpYkjGeEvN76ML2KbnoDwiWhLPEC0JNK8MLNPb3REtx26/Ln04FKVXo9tZYjrTKXmxpwGd+fvB+9239CPBvylPsGLBoQMQ8D/r2SS8cyAKrlclKmZsyiH+WWXO4UjURRhLAeXR2gWh4U6PnW1CXE2diBo4/z6MTJ7cw73hIM5ydEgHkrEvwndYeT+iXtwuWpdN02sfR/UQPnrohSqZnvcT3vYffwZbWQk+kYe4+L8oVUECKyGKo+gmjkkOyLc60Ylry2ZC1varLoMSwRAgMBAAECggEAEZtJF3CsvzsFUY0hQOKrFqbLcRjMm6HddwxaGb9rpfKf+Iu8MAKB+87zmJqoUyd7suHl4UJnTf/udjkdjnv6YdJjxTEhSfeEh3JMqO5pDTtNHploDxaJyj55cKQe+WAbqLa4qPC9nibUvbyarKczM1GhfN+Nuuxo/QVQdVuag+42nM/S8cjJJh6jo69iVS2H7LprflQbNArGWN9qHNXwKM3mWjADF4xfV4LNi/OsXX9vjF1b7yiH/a9kv1vosq0GS2DWtNvd1+TY5sKKV9CZBoBRd0xA9PqRKQ4MAabQhQ5OBbmQ4/sWYaeBBoKUn3U93hfc+P8je1bQveVQVFQxVwKBgQDidfsRRn5MCyKuCGlYenTwQH3OmjC6qbhIUku9WzdNUEaxJr0iKbiXhjZjB/vy9g6GHnzTJ4xFKuQEyyWQG+zLmyfC0DRf+U9bATqVwi5T/If0XQ5wqo5MjHPtDR39K/pLW+wiZrs4po+gq6o0osV56UUBRMB8t0/sqcfsDcW4qwKBgQDVA6jM9E/QY0vn0iEn+jH7764KzrGFCF3p6L+4XIw8mbKCmR862HPOexxLnE9bGDVibReE27zdzutzvoeGUSnC56Ouk9RpbwWhmTOLP+djgrQNpLVxI6cT0SzfqnPNnqtR8MoY3ZJaqSY4lYQFmYfVaFAdBgVDDJdKgQeVOSwmMwKBgEImxJhfRzVfa1n7CwrVeqNTs2xOjj14pmQ55fYCVz02XfARqN354fohMnHrOyXVyphS/5OO0eLCjKj5zpcyERHI2OyHdUUzxoKG8V4dwvq1oeE37afrqnWh8ZslYcU6u3qX93p1F+uMfBgrDSUjBxx9j7K3KqNDyQ0Q612BCGjRAoGANbbchAI/dh7z7xsvvTL8E7mWu6bvYMqBVBCa99RukIF5YDFYjLA0U6b7tZ1O7XunSpCT067Na2lYOjGbXyVsUHe08LraX1PdqahGNSECKje1S5NzJXqGERs4I5aJ6RnPvbPoYmjNFQt+VdpuFjNm60uImCkCfqvYIrNyxBBMr70CgYEAhHBqi/r6JZJQ+T3oMNr+OKE/czxCBrrFI5kRStHLMZ2UxkgpFQd5Dw1WIlBdmm1tO6W8VRVEsh2aWx7obissp8cwukTrpP73aS721sXt0NR2kl8GIgD8MVtG8TuMxgKH8kBgdyUNLD8FHTyYsmwJDHY6jp9aOn/0J2GEn7jwKNU=";

    private static final String PUBLIC_KEY_PRO = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmkaI/C0jIV5r6o4meTyGEKGzuRLhG6koRr6+eemuyBiCwRUq2TbaRzq1OKZK9a8QuAkDRb+XbmOn4fgJTBPcCgD1gucoJHCc+jo9eqGux6XaoYNi6onHX+iGh1KaLEI6/G4lMntefWWptUzL7gJo+MzGs2pv2C6ERmQTMlfn5QJ44Thj8QJRA0WF4nuxY7lV3zZd61FEcC6a7qg3+bsEo/wfvGP2f9lOf3oxgBB+an3hNbFugtRWhzBGepGDTb/VemAgaL5Rqi25R3bpRnc6PJIaoCmbM+q7ed5yYpa+vfOraya10EwVfgwbg+gmQAa/95vh8sjpPuvdjv+YF1OaEQIDAQAB";
    private static final String PRIVATE_KEY_PRO = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCaRoj8LSMhXmvqjiZ5PIYQobO5EuEbqShGvr556a7IGILBFSrZNtpHOrU4pkr1rxC4CQNFv5duY6fh+AlME9wKAPWC5ygkcJz6Oj16oa7Hpdqhg2Lqicdf6IaHUposQjr8biUye159Zam1TMvuAmj4zMazam/YLoRGZBMyV+flAnjhOGPxAlEDRYXie7FjuVXfNl3rUURwLpruqDf5uwSj/B+8Y/Z/2U5/ejGAEH5qfeE1sW6C1FaHMEZ6kYNNv9V6YCBovlGqLblHdulGdzo8khqgKZsz6rt53nJilr6986trJrXQTBV+DBuD6CZABr/3m+HyyOk+692O/5gXU5oRAgMBAAECggEAcXa++PkZhUk0hWXW1gO1djX3QH6qIPXgMWfIH1HGwlOElw1CXk8BarG65tcm+lqvKSs3xOyMKxwQNRl/fPx+ML1T09q+o05PpZl/7dlL+nn4uiEvdt+uStNxXcVaroJnb5ByuxYRAJjsn/LDaWFMYu/4k6wMZiqQDlU76SLN4pcwVYmuHdDXOOiko7HJHcKGgwpQwtmFis8tUtPFTbEp11orY2JIQBKHxwWPhFDHeSYm6XhVcxR8qZMdczKU7UY70RPaSNGEwe+Zf8X+FuYG6eBkHL3zrO6ZR91DPftaefmBXf37wVVu+qgtxMe2JTXgT0eYD/X6wj+GDr3PZ9+YIQKBgQDgwsnS8hqgQ9niWKfHFDuu3S9BZ1+qcd4x4KNLPVfo61ucBMukafY4reGdfT9Fsifveg6LPw/Q31XpiOnGPE7nbnqPUIOZBr5EndIJLKmFjqco3w4I4XDFe1P/uCdsDggh6HjTcG03C8RtOC8SvkcsqMxStI82KnGgbAr4kAD5twKBgQCvt88KeesFNTPSCp9Jnf4IiNfM3McGXIfxkDOewninK17EvUIsxGCO0imaX940CTTCcap05qmgpZoqmkvgFJGOXQ2+fcJnS4BOTEEp4Rm7OSjyiMxIoT13FgC4+AdDcpYdgScu0SRAuXRgrvMZ+0owk7q+PSxtppZSpUAt91SqdwKBgEApu135+XnpBMlmD7d4YjVgNT5RmXDaAtlwTI2NHIyEq+lVQSFVkpHJqc/A0pA99MKbsI012nkBWNBLYplR3CCbXGePT5Ie57BE8ZHm6xoSiEvVqXcFjKEDvQ4Sb0acltu/Nsb9oGAHfvLDCEUshrsMERBHNV78LOXkEhm1WmHvAoGASQ4DXxinFGg1HjtaVDICfqveBIc5m7R2tIC+ZV/255GJegQxcglHF7naoBsBd+Ln83tsYePQXEDdYp75srT6exTG9Q6191oVgvgDE5VJrGQsWQF05TFcK5mTPVOpOWp3bNbYYoeDKgGwl83EtmWaR63zssoeIYxj7QJVN4cfxSsCgYEApLP/DRoWa9KpyUkrpTIveahN7SwTZP9PxTCKNrSchoJbkes809oYWQXoQxSlovPpsnxcfbeVJt7AIpFT51XrxVlaGxrqj2UCiQUzff48duGcfdtjpdsDZQD0C3Do74RFIRlbRQ17uKeSoUzhSAPXFl8m+5TXuGjMWzB+90fEyqQ=";

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
