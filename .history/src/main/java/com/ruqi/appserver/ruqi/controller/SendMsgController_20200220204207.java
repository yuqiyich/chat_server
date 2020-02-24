package com.ruqi.appserver.ruqi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendMsgController {
    @RequestMapping(value = "/getData212", method = RequestMethod.GET)
	// @ResponseBody
    public String home(String type) {
		return String.format("input: %s, output: 12312", type);
	}
}