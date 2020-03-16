package com.ruqi.appserver.ruqi.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruqi.appserver.ruqi.network.BaseHttpClient;
import com.ruqi.appserver.ruqi.network.UrlConstant;
import com.ruqi.appserver.ruqi.network.WechatConstant;
import com.ruqi.appserver.ruqi.service.WechatService;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@ApiIgnore
@Api(tags = "微信H5模块")
@RequestMapping(value = "/wechatH5")

// 在html页面并没有办法获取的到控制器ModelAndView里面数据的方法
public class WechatWebController {

    @Autowired
    private WechatService wechatService;

    /**
     * 扫码，打开网址后内部调用微信的authorize接口得到对应的CODE，会触发打开回调URL，里面能拿到CODE。
     * 这个接口可做一些判断校验处理，如过期失效等。
     */
    @RequestMapping(value = "/authorize")
    public String authorize(Model model) {
        // 跳转到打开微信的url
        String redirectUri = WechatConstant.REDIRECT_URI_OPEN_ID;
        try {
            redirectUri = URLEncoder.encode(redirectUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = String.format(UrlConstant.WeChatUrl.USER_AUTHORIZE, WechatConstant.APP_ID, redirectUri);

        model.addAttribute("url", url);
        return "wechatAuthorize";
    }

    /**
     * 扫码，打开网址后内部调用微信的authorize接口得到对应的CODE，会触发打开回调URL，里面能拿到CODE。这个接口可做一些判断校验处理，如过期失效等。
     *
     * @return
     */
    @RequestMapping(value = "/redirectUri")
    public String redirectUri(String code) {
        // 拿到对应的openId，与某个微信进行绑定，能获取到微信昵称等？ 保存在数据库，后续发送模板消息使用这个openId。

        if (MyStringUtils.isEmpty(code)) {
            System.out.println("--->redirectUri code is null");
            return "error";
        }

        String result = BaseHttpClient.sendGet(String.format(UrlConstant.WeChatUrl.OPEN_ID_GET,
                WechatConstant.APP_ID, WechatConstant.SECRET, code), "");

        System.out.println("--->openIdResult=" + result);

        String accessToken = "";
        String openId = "";
        try {
            JSONObject json = JSONObject.parseObject(result);
            if (json.containsKey("access_token")) {
                accessToken = json.getString("access_token");
            }
            if (json.containsKey("openid")) {
                openId = json.getString("openid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!MyStringUtils.isEmpty(openId)) {
            // 解析拿到openId，和token，再去获取用户信息拿到昵称
            String userInfo = BaseHttpClient.sendGet(String.format(UrlConstant.WeChatUrl.USER_INFO_GET,
                    accessToken, openId), "");

            System.out.println("--->userInfoResult=" + userInfo);

            String nickname = "";
            try {
                JSONObject json = JSONObject.parseObject(userInfo);
                if (json.containsKey("nickname")) {
                    nickname = json.getString("nickname");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // openId与昵称成对，保存在数据库。openId是唯一的不应该重复，先查询数据库是否有数据，有的话设为绑定状态即可；不存在则新增一个数据。
            wechatService.bindReceiver(openId, nickname);
            return "wechatRedirectSuccess";
        }

        return "error";
    }

}