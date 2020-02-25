package com.ruqi.appserver.ruqi.network;

public class UrlConstant {
    //-------------------微信-------------------
    public class WeChatUrl {
        // 获取openId
        public final static String OPEN_ID_GET = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

        // 获取token
        public final static String TOKEN_GET = "https://api.weixin.qq.com/cgi-bin/token";
        // 服务号模板消息发送
        public final static String TEMPLATE_MSG_SEND = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    }

}