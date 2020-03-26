package com.ruqi.appserver.ruqi.interceptor;

import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * swagger关闭状态，查看该url时直接跳转到错误界面
 * 网页根据是否登录进行跳转处理
 */
public class UrlInterceptor extends BaseInterceptor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${swagger.enable:false}")
    private boolean mSwaggerEnable = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String url = request.getRequestURL().toString();

//        logger.info("mSwaggerEnable:" + mSwaggerEnable);
        if (url.contains("swagger-ui.html")) {
            if (!mSwaggerEnable) {
                String newUrl = "/error";
                response.sendRedirect(newUrl);
                return false;
            } else {
                return true;
            }
        }

        String userToken = getToken(request);

        if (MyStringUtils.isEmpty(userToken)) {
            if (!url.contains("/login.html") && (!url.contains(".html") || url.contains("/main.html"))) {
                String newUrl = "/login.html";
                response.sendRedirect(newUrl);
                return false;
            }
        } else {
            boolean isValid = isTokenValid(userToken);
            if (isValid && url.contains("/login.html")) {
                String newUrl = "/main.html";
                response.sendRedirect(newUrl);
                return false;
            } else if (!isValid && !url.contains("/login.html")) {
                String newUrl = "/login.html";
                response.sendRedirect(newUrl);
                return false;
            }
        }

        return true;
    }
}