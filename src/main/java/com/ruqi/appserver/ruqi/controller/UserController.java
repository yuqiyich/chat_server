package com.ruqi.appserver.ruqi.controller;


import com.ruqi.appserver.ruqi.bean.UserEntity;
import com.ruqi.appserver.ruqi.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "用户访问模块")
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "获取所有用户")
    @RequestMapping(value = "/findall", method = RequestMethod.GET)
    @ResponseBody
    public List<UserEntity> findAll() {
        return userService.findAll();
    }
}
