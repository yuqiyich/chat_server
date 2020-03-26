package com.ruqi.appserver.ruqi.interceptor;

import com.alibaba.fastjson.JSON;
import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.dao.entity.UserInfoEntity;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import com.ruqi.appserver.ruqi.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口拦截器，需要有token才能请求接口
 */
public class APIInterceptor extends BaseInterceptor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String url = request.getRequestURL().toString();

        if (url.contains("swagger")) {
            return true;
        }

        // 区分，本地存在接口的，返回统一token错误的json。否则跳转到登录界面去或者404处理。
        if (handler instanceof HandlerMethod) {
            String token = getToken(request);
            if (!isTokenValid(token)) {
                logger.info("--->APIInterceptor token invalid url=" + url + ", handler=" + handler);

                BaseBean result = new BaseBean();
                result.errorCode = ErrorCodeMsg.ERROR_USER_TOKEN.errorCode;
                result.errorMsg = ErrorCodeMsg.ERROR_USER_TOKEN.errorMsg;
                response.setContentType("text/javascript; charset=utf-8"); // json数据
                response.getWriter().write(JSON.toJSONString(result));
                return false;
            } else {
                // 优先redis中读取用户信息。
                UserInfoEntity userInfoEntity = userService.findUserByToken(token);
                request.setAttribute("userData", userInfoEntity);
            }
        }
//        else {
//            String newUrl = "/login.html";
//            response.sendRedirect(newUrl);
//            return true;
//        }
        return true;
    }
}