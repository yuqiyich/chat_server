package com.ruqi.appserver.ruqi.config;


import com.ruqi.appserver.ruqi.network.RequestWrapper;
import com.ruqi.appserver.ruqi.network.ResponseWrapper;
import com.ruqi.appserver.ruqi.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @program: springboot
 * @description:
 * @author: Sid
 * @date: 2018-11-19 09:21
 * @since: 1.0
 **/
@Order(0)
/**
 * 注册过滤器
 * */
@WebFilter(filterName = "RequestResponseLogFilter", urlPatterns = "/*")
public class RequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getQueryString();
        String servletPath = request.getServletPath();
        String url = request.getRequestURI();
        RequestWrapper requestWrapper = null;

        logger.info("========================》  IP:" + IpUtil.getIpAddr(request));
        StringBuilder sb = new StringBuilder();
        if (request instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper(request);
            BufferedReader bufferedReader = requestWrapper.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        }

        ResponseWrapper responseWrapper=new ResponseWrapper( response);

        if (null == requestWrapper) {
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(requestWrapper, responseWrapper);
        }
        logger.info("========================》  url:" + url + " & queryString:" + path+" & servletPath:"+servletPath);
        logger.info("========================》request uri: {}",request.getRequestURI());
        logger.info("========================》request ContentType: {}",request.getContentType());
        logger.info("========================》request param: {}",sb.toString());

        logger.info("========================》response status: {}",response.getStatus());
        logger.info("========================》response ContentType: {}",response.getContentType());


        String result=new String(responseWrapper.getResponseData());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(result.getBytes());
        outputStream.flush();
        outputStream.close();
        // 打印response
        logger.info("========================》response return data: {} \t" + result);

    }

}