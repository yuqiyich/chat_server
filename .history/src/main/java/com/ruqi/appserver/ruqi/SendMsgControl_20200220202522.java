package com.ruqi.appserver.ruqi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SendMsgControl {
    @RequestMapping(value = "/getData212", method = RequestMethod.GET)
	@ResponseBody
    public String home(String type) {
		return String.format("input: %s, output: 12312", type);
	}
}