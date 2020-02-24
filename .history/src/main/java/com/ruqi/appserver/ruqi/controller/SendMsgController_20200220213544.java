package com.ruqi.appserver.ruqi.controller;

import java.io.IOException;
import org.json.JSONException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMsgController {
	// 短信应用 SDK AppID
	int appid = 1400318874; // SDK AppID 以1400开头
	// 短信应用 SDK AppKey
	String appkey = "04937fba614f839280720eff200868c3";
	// 需要发送短信的手机号码
	String[] phoneNumbers = {"13076898090"};
	// 短信模板 ID，需要在短信应用中申请
	int templateId = 538030; // NOTE: 这里的模板 ID`7839`只是示例，真实的模板 ID 需要在短信控制台中申请
	// 签名
	String smsSign = "腾讯云"; // NOTE: 签名参数使用的是`签名内容`，而不是`签名ID`。这里的签名"腾讯云"只是示例，真实的签名需要在短信控制台申请

    @RequestMapping(value = "/sendMsg", method = RequestMethod.GET)
	// @ResponseBody
    public String sendMsg(String type) {
		try {
			String[] params = {"5678", "1234"};
			SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
			SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
				templateId, params, smsSign, "", "");
			System.out.println(result);
		  } catch (HTTPException e) {
			// HTTP 响应码错误
			e.printStackTrace();
		  } catch (JSONException e) {
			// JSON 解析错误
			e.printStackTrace();
		  } catch (IOException e) {
			// 网络 IO 错误
			e.printStackTrace();
		  }

		return String.format("input: %s, output: 5678", type);
	}
}