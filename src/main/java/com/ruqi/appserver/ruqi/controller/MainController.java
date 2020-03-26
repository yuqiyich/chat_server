package com.ruqi.appserver.ruqi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class MainController {
    @Value("${spring.profiles.active}")
    private String mEnv = "";

    @RequestMapping(value = "/main.html")
    public String index(Model model) {
        model.addAttribute("env", mEnv);
        return "main";
    }
}