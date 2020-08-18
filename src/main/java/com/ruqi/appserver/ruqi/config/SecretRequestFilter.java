package com.ruqi.appserver.ruqi.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruqi.appserver.ruqi.bean.EncryptResponse;
import com.ruqi.appserver.ruqi.request.EncryptBaseRequest;
import com.ruqi.appserver.ruqi.service.RedisUtil;
import com.ruqi.appserver.ruqi.utils.AESUtils;
import com.ruqi.appserver.ruqi.utils.Base64Util;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Cipher;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @program: springboot
 * @description:
 * @author: liangbingkun
 * @date: 2018-11-19 09:21
 * @since: 1.0
 **/
@Order(1)
/**
 * 注册过滤器
 * */
@WebFilter(filterName = "SecretRequestFilter", urlPatterns = "/point/*")
public class SecretRequestFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(SecretRequestFilter.class);
    private static final String REQ = "req";
    private static final String SIGN = "sign";

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestBody = getRequestBody((HttpServletRequest) request);
        logger.info("request body:" + requestBody);
        //解密请求报文
        String[] requestData = decryptRequestBody(requestBody);

        WrapperedRequest wrapRequest = new WrapperedRequest(request, requestData[0]);
        WrapperedResponse wrapResponse = new WrapperedResponse(response);
        chain.doFilter(wrapRequest, wrapResponse);
        byte[] data = wrapResponse.getResponseData();
        String responseBody = new String(data, "utf-8");
        logger.info("加密前原始返回数据： " + responseBody);
        responseBody = encryResponse(requestData[1], responseBody);
        writeResponse(response, responseBody);
    }

    private String encryResponse(String aesKey, String responseBody){
        // 加密返回报文
        if(!StringUtils.isEmpty(aesKey)){
            logger.info("返回结果需要加密");
            String encryResponse = AESUtils.des(responseBody, aesKey, Cipher.ENCRYPT_MODE);
            EncryptResponse encryptResponse = new EncryptResponse(true);
            encryptResponse.data = encryResponse;
            responseBody = JsonUtil.beanToJsonStr(encryptResponse);
            logger.info("加密后返回的数据： " + responseBody);
        }else{
            logger.info("返回结果是明文");
            logger.info("明文返回的数据： " + responseBody);
        }
        return responseBody;
    }

    private String[] decryptRequestBody(String body){
        String[] result = new String[2];
        result[0] = body;
        JSONObject jsonObject = null;
        try{
            jsonObject = JSON.parseObject(body);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("json parse error："+ e.getMessage());
        }
        if(jsonObject != null && jsonObject.containsKey(REQ) && jsonObject.containsKey(SIGN)){
            EncryptBaseRequest encryptBaseRequest = JsonUtil.jsonStrToBean(EncryptBaseRequest.class, body);
            String sign = encryptBaseRequest.getSign();
            String aesKey = String.valueOf(redisUtil.getKey(RedisUtil.GROUP_ENCRYPT_UTIL_SIGN, sign));
            logger.info("aesKey："+ aesKey);
            result[0] = AESUtils.des(encryptBaseRequest.getReq(), aesKey, Cipher.DECRYPT_MODE);
            result[1] = aesKey;
            logger.info("请求数据是密文");
            logger.info("解密后的请求数据："+ result[0]);
        }else{
            logger.info("请求数据是明文");
            logger.info("请求数据："+ body);
        }
        return result;
    }

    private String getRequestBody(HttpServletRequest req) {
        try {
            BufferedReader reader = req.getReader();
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return json;
        } catch (IOException e) {
            logger.info("请求体读取失败"+e.getMessage());
        }
        return "";
    }

    private void writeResponse(ServletResponse response, String responseString)
            throws IOException {
        PrintWriter out = response.getWriter();
        out.print(responseString);
        out.flush();
        out.close();
    }

}