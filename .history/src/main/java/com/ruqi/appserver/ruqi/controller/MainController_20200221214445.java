package com.ruqi.appserver.ruqi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	@RequestMapping(value = "/main")
    public ModelAndView main() {
		ModelAndView modelAndView = new ModelAndView("main");
		// modelAndView.addObject("attributeName", attributeValue);
		return modelAndView;
	}
}