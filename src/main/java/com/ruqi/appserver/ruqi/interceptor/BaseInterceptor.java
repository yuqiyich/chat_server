package com.ruqi.appserver.ruqi.interceptor;

import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BaseInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

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
        return userToken;
    }

    protected boolean isTokenValid(HttpServletRequest request) {
        return isTokenValid(getToken(request));
    }

    protected boolean isTokenValid(String token) {
        if (MyStringUtils.isEmpty(token)) {
            return false;
        }
        List<String> tokenList = new ArrayList<>();
        // TODO: 2020/3/26 redis读取数据库的处理 
        tokenList.add("8a243fba1fed17178262d074ce2b3255");
        tokenList.add("6725d22ad3048777cdf9cfe9424579c2");
        return tokenList.contains(token);
    }

}