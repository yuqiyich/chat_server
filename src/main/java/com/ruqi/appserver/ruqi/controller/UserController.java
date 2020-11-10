package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.UserEntity;
import com.ruqi.appserver.ruqi.dao.entity.LoginInfoEntity;
import com.ruqi.appserver.ruqi.dao.entity.UserInfoEntity;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import com.ruqi.appserver.ruqi.service.IUserService;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import com.ruqi.appserver.ruqi.utils.Md5Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "用户访问模块")
@RequestMapping(value = "/user")
public class UserController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @ApiOperation(value = "获取所有用户")
    @RequestMapping(value = "/findall", method = RequestMethod.GET)
    @ResponseBody
    public List<UserEntity> findAll() {
        return userService.findAll();
    }

    @ApiOperation(value = "登录", notes = "登录，返回用户信息、token等")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "UserInfoEntity",
                    name = "userInfoEntity", value = "用户登录bean，账号密码等", required = true, paramType = "body")
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<UserInfoEntity> login(HttpServletRequest request, HttpServletResponse response, @RequestBody UserInfoEntity userInfoEntity) {
        BaseBean<UserInfoEntity> result = new BaseBean();
        userInfoEntity.password = Md5Util.commonMd5(userInfoEntity.password);
//        logger.info("--->password=" + userInfoEntity.password);
        UserInfoEntity userInfoEntityResult = userService.findUser(userInfoEntity);
        if (null != userInfoEntityResult) {
            if (userInfoEntityResult.userIsValid()) {
                // 数据库存在用户。这里简单的数据库固定死token，返回给前端，前端自己保存和销毁；定时token每天更新变化一次，简单的安全防护。
                // 如果想做单点登录可以每次都重置token值更新数据库；不做单点登录则可以只读取token，token需要有有效期则另外处理。
                result.data = userInfoEntityResult;

                // 在客户端存储用户个性化信息，方便用户下次再访问网站时使用
                try {
                    Cookie cookie = new Cookie("token", URLEncoder.encode(userInfoEntityResult.token, "utf-8"));
                    cookie.setPath(contextPath);
                    cookie.setMaxAge(60 * 60 * 24 * 7); // cookie有效期7天
                    response.addCookie(cookie);

                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", userInfoEntityResult);

                    redisUtil.putKey(RedisUtil.GROUP_USER_INFO, userInfoEntityResult.token,
                            userInfoEntityResult, RedisUtil.EXPIRE_WEEK, TimeUnit.SECONDS);
                } catch (Exception e) {
                    logger.info("--->Exception:" + e);
                    e.printStackTrace();
                }
            } else {
                result.errorCode = ErrorCodeMsg.ERROR_INVALID_USER.errorCode;
                result.errorMsg = ErrorCodeMsg.ERROR_INVALID_USER.errorMsg;
            }
        } else {
            result.errorCode = ErrorCodeMsg.ERROR_NO_USER.errorCode;
            result.errorMsg = ErrorCodeMsg.ERROR_NO_USER.errorMsg;
        }
        return result;
    }

    @ApiOperation(value = "登录", notes = "登录，返回用户信息、token等")
    @RequestMapping(value = "/newlogin", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public BaseBean<UserInfoEntity> newlogin(@Validated @RequestBody LoginInfoEntity request, BindingResult bindingResult) {
        logger.info("--->newlogin request:" + request);
        BaseBean<UserInfoEntity> result = new BaseBean();
        if (bindingResult.hasErrors()) {
            result.errorCode = ErrorCodeMsg.ERROR_INVALID_PARAMS.errorCode;
            result.errorMsg = bindingResult.getFieldError().getDefaultMessage();
        } else {
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.account = request.account;
            userInfoEntity.password = Md5Util.commonMd5(request.password);
            UserInfoEntity userInfoEntityResult = userService.findUser(userInfoEntity);
            if (null != userInfoEntityResult) {
                if (userInfoEntityResult.userIsValid()) {
                    // 数据库存在用户。这里简单的数据库固定死token，返回给前端，前端自己保存和销毁；定时token每天更新变化一次，简单的安全防护。
                    // 如果想做单点登录可以每次都重置token值更新数据库；不做单点登录则可以只读取token，token需要有有效期则另外处理。
                    result.data = userInfoEntityResult;

                    // 在客户端存储用户个性化信息，方便用户下次再访问网站时使用
                    redisUtil.putKey(RedisUtil.GROUP_USER_INFO, userInfoEntityResult.token,
                            userInfoEntityResult, RedisUtil.EXPIRE_WEEK, TimeUnit.SECONDS);
                } else {
                    result.errorCode = ErrorCodeMsg.ERROR_INVALID_USER.errorCode;
                    result.errorMsg = ErrorCodeMsg.ERROR_INVALID_USER.errorMsg;
                }
            } else {
                result.errorCode = ErrorCodeMsg.ERROR_NO_USER.errorCode;
                result.errorMsg = ErrorCodeMsg.ERROR_NO_USER.errorMsg;
            }
        }
        return result;
    }

    @ApiOperation(value = "获取用户信息", notes = "根据token查询用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public BaseBean<UserInfoEntity> getUserInfo(HttpServletRequest request) {
        BaseBean<UserInfoEntity> result = new BaseBean();
        UserInfoEntity userInfoEntity = (UserInfoEntity) request.getAttribute("userData");
//        logger.info("--->/user/info userInfoEntity:" + userInfoEntity);
        if (null != userInfoEntity) {
//            logger.info("userInfoEntity.userIsValid():" + userInfoEntity.userIsValid());
            if (userInfoEntity.userIsValid()) {
                // 数据库存在用户。这里简单的数据库固定死token，返回给前端，前端自己保存和销毁；定时token每月1号0点更新变化一次，简单的安全防护。
                // 如果想做单点登录可以每次都重置token值更新数据库；不做单点登录则可以只读取token，token需要有有效期则另外处理。
                result.data = userInfoEntity;

                // 在客户端存储用户个性化信息，方便用户下次再访问网站时使用。
                try {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", userInfoEntity);
//                    session.setAttribute("username", userInfoEntity.nickname);

//                    logger.info("--->/user/info session:" + session.getAttribute("user").toString());
                } catch (Exception e) {
                    logger.info("--->Exception:" + e);
                    e.printStackTrace();
                }
            } else {
                result.errorCode = ErrorCodeMsg.ERROR_INVALID_USER.errorCode;
                result.errorMsg = ErrorCodeMsg.ERROR_INVALID_USER.errorMsg;
            }
        } else {
            result.errorCode = ErrorCodeMsg.ERROR_NO_USER.errorCode;
            result.errorMsg = ErrorCodeMsg.ERROR_NO_USER.errorMsg;
        }
        return result;
    }
}
