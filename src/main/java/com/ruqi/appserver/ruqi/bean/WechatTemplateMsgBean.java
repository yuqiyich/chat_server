package com.ruqi.appserver.ruqi.bean;

public class WechatTemplateMsgBean {
    // 接收者openid 必填
    public String touser;
    // 模板ID 必填
    public String template_id;

    // // 模板跳转链接（海外帐号没有跳转能力）
    // public String url;

    // // 跳小程序所需数据，不需跳小程序可不用传该数据
    // public String miniprogram;
    // // 所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏） 必填
    // public String appid;
    // // 所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏
    // public String pagepath;

    // 模板数据 必填
    public TemplateData data;

    public static class TemplateData {
        public TemplateItemData first;
        public TemplateItemData keyword1;
        public TemplateItemData keyword2;
        public TemplateItemData keyword3;
        public TemplateItemData remark;
    }

    public static class TemplateItemData {
        public String value;
        public String color;
    }
}