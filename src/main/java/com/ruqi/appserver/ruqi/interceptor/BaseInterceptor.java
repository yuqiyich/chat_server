package com.ruqi.appserver.ruqi.interceptor;

import com.ruqi.appserver.ruqi.service.IUserService;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public class BaseInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    protected final String URL_LOGIN = "login.html";
    protected final String URL_MAIN = "main.html";
    protected final String URL_ERROR = "error";

    @Autowired
    protected IUserService userService;

    @Autowired
    protected RedisUtil redisUtil;

    protected void redirtTo(HttpServletRequest request, HttpServletResponse response, String url) {
        logger.info("--->request.url=" + request.getRequestURL().toString() + ", redirtTo:" + url);
        //跳转到登录页面
        response.setStatus(302);//或者303,兼容http1.1
        response.setHeader("location", url);
    }

    protected String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String userToken = null;
        if (null != cookies) {
            for (Cookie c : cookies) {
                String name = c.getName();
                if (name.equals("token")) {
                    userToken = c.getValue();
                    break;
                }
            }
        }
        // 新方式，旧版判断后续会去除
        if (MyStringUtils.isEmpty(userToken)) {
            userToken = request.getHeader("token");
        }
        return userToken;
    }

    protected boolean isTokenValid(HttpServletRequest request) {
        return isTokenValid(getToken(request));
    }

    protected boolean isTokenValid(String token) {
        if (MyStringUtils.isEmpty(token)) {
            return false;
        }

        boolean existsKey = redisUtil.existsKey(RedisUtil.GROUP_USER_INFO, token);

//        logger.info("--->existsKey " + token + ":" + existsKey);
        return existsKey;
    }

}
