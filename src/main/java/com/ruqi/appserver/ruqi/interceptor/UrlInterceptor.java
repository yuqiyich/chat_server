package com.ruqi.appserver.ruqi.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * swagger关闭状态，查看该url时直接跳转到错误界面
 */
public class UrlInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${swagger.enable:false}")
    private boolean mSwaggerEnable = false;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String url = request.getRequestURL().toString();

//        logger.info("mSwaggerEnable:" + mSwaggerEnable);
        if (!mSwaggerEnable && url.contains("swagger-ui.html")) {
            String newUrl = "/error";
            response.sendRedirect(newUrl);
            return false;
        }

        return true;
    }
}