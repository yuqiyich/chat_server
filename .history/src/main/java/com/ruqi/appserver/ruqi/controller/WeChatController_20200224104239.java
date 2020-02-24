package com.ruqi.appserver.ruqi.controller;

import java.io.IOException;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import org.json.JSONException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "微信模块")
public class WeChatController {

	@ApiOperation(value = "微信公众号模板消息发送", notes = "报警通知")
	// @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "参数1", required = true, dataType = "String")})
	@RequestMapping(value = "/sendAliyunMsg", method = RequestMethod.GET)
	// @ResponseBody
    public String sendAliyunMsg(String type) {
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", mAccessKeyId, mAccessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

		CommonRequest request = new CommonRequest();
		request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mPhoneNumbers);
        request.putQueryParameter("SignName", mSignName);
        request.putQueryParameter("TemplateCode", mTemplateCode);
        request.putQueryParameter("TemplateParam", "{\"deviceId\":\"123124\", \"riskType\":\"多开\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

		return String.format("input: %s, output: 5678", type);
	}
}