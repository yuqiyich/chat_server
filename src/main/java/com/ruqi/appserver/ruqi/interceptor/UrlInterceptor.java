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

    @Value("${swagger.enable:true}")
    private boolean mSwaggerEnable = true;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String url = request.getRequestURL().toString();

//        logger.info("mSwaggerEnable:" + mSwaggerEnable);
        if (url.contains("swagger-ui.html")) {
            if (!mSwaggerEnable) {
                redirtTo(request, response, URL_ERROR);
                return false;
            } else {
                return true;
            }
        }

        String userToken = getToken(request);

//        logger.info("userToken:" + userToken);
        if (MyStringUtils.isEmpty(userToken)) {
            if (!url.contains(URL_LOGIN) && (!url.contains(".html") || url.contains(URL_MAIN))) {
                redirtTo(request, response, URL_LOGIN);
                return false;
            }
        } else {
            boolean isValid = isTokenValid(userToken);
//            logger.info("userToken isValid:" + isValid + ", url:" + url);
            if (isValid && url.contains(URL_LOGIN)) {
                redirtTo(request, response, URL_MAIN);
                return false;
            } else if (!isValid && !url.contains(URL_LOGIN)) {
                redirtTo(request, response, URL_LOGIN);
                return false;
            }
        }

        return true;
    }

}
