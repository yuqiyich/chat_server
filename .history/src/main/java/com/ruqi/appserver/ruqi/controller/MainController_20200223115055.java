package com.ruqi.appserver.ruqi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	@RequestMapping(value = "")
    public ModelAndView index() {
		ModelAndView modelAndView = new ModelAndView("index");
		// modelAndView.addObject("attributeName", attributeValue);
		return modelAndView;
	}
	
	@RequestMapping(value = "/main")
    public ModelAndView main() {
		ModelAndView modelAndView = new ModelAndView("layuiAdmin");
		// modelAndView.addObject("attributeName", attributeValue);
		return modelAndView;
	}
	
}