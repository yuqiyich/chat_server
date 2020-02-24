package com.ruqi.appserver.ruqi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "微信模块")
@RequestMapping(value = "/wechat")
public class WeChatController {

	@ApiOperation(value = "微信公众号模板消息发送", notes = "报警通知")
	// @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "参数1", required = true, dataType = "String")})
	@RequestMapping(value = "/template/send", method = RequestMethod.POST)
	// @ResponseBody
    public String sendTemplateMsg(String type) {
		return String.format("input: %s, output: 5678", type);
	}
}