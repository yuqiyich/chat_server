//package com.ruqi.appserver.ruqi.controller;
//
//import java.io.IOException;
//
//import com.aliyuncs.CommonRequest;
//import com.aliyuncs.CommonResponse;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.exceptions.ServerException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//import com.github.qcloudsms.SmsSingleSender;
//import com.github.qcloudsms.SmsSingleSenderResult;
//import com.github.qcloudsms.httpclient.HTTPException;
//
//import org.json.JSONException;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//
//@RestController
//@Api(value = "短信模块")
//public class SendMsgController {
//	// 接收短信的手机号码，逗号隔开可多个
//	private String mPhoneNumbers = "13076898090";
//	// 平台名
//	private String mSignName = "如祺";
//
//	//-----------------------------阿里云短信-----------------------------
//	private String mAccessKeyId = "LTAI4FjDehvg62XM5XNX11zD";
//	private String mAccessSecret = "HgTQAK0SKh85mgAb8C6ui3AjCZ3lxq";
//	private String mTemplateCode = "SMS_183798722";
//	//-----------------------------阿里云短信-----------------------------
//
//	//-----------------------------腾讯云短信-----------------------------
//	// 短信应用 SDK AppID
//	int appid = 1400318874; // SDK AppID 以1400开头
//	// 短信应用 SDK AppKey
//	String appkey = "04937fba614f839280720eff200868c3";
//	// 需要发送短信的手机号码
//	String[] phoneNumbers = {"13076898090"};
//	// 短信模板 ID，需要在短信应用中申请
//	int templateId = 538030; // NOTE: 这里的模板 ID`7839`只是示例，真实的模板 ID 需要在短信控制台中申请
//	// 签名
//	String smsSign = "腾讯云"; // NOTE: 签名参数使用的是`签名内容`，而不是`签名ID`。这里的签名"腾讯云"只是示例，真实的签名需要在短信控制台申请
//	//-----------------------------腾讯云短信-----------------------------
//
//    @RequestMapping(value = "/sendTecentMsg", method = RequestMethod.GET)
//	// @ResponseBody
//    public String sendTecentMsg(String type) {
//		try {
//			String[] params = {"5678", "1234"};
//			SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
//			SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
//				templateId, params, smsSign, "", "");
//			System.out.println(result);
//		  } catch (HTTPException e) {
//			// HTTP 响应码错误
//			e.printStackTrace();
//		  } catch (JSONException e) {
//			// JSON 解析错误
//			e.printStackTrace();
//		  } catch (IOException e) {
//			// 网络 IO 错误
//			e.printStackTrace();
//		  }
//
//		return String.format("input: %s, output: 5678", type);
//	}
//
//	@ApiOperation(value = "阿里云平台发送短信", notes = "阿里云平台发送短信 notes")
//	@ApiImplicitParams({@ApiImplicitParam(name = "type", value = "参数1", required = true, dataType = "String")})
//	@RequestMapping(value = "/sendAliyunMsg", method = RequestMethod.GET)
//	// @ResponseBody
//    public String sendAliyunMsg(String type) {
//		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", mAccessKeyId, mAccessSecret);
//        IAcsClient client = new DefaultAcsClient(profile);
//
//		CommonRequest request = new CommonRequest();
//		request.setMethod(MethodType.POST);
//        request.setDomain("dysmsapi.aliyuncs.com");
//        request.setVersion("2017-05-25");
//        request.setAction("SendSms");
//        request.putQueryParameter("RegionId", "cn-hangzhou");
//        request.putQueryParameter("PhoneNumbers", mPhoneNumbers);
//        request.putQueryParameter("SignName", mSignName);
//        request.putQueryParameter("TemplateCode", mTemplateCode);
//        request.putQueryParameter("TemplateParam", "{\"deviceId\":\"123124\", \"riskType\":\"多开\"}");
//        try {
//            CommonResponse response = client.getCommonResponse(request);
//            System.out.println(response.getData());
//        } catch (ServerException e) {
//            e.printStackTrace();
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
//
//		return String.format("input: %s, output: 5678", type);
//	}
//}