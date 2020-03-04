package com.ruqi.appserver.ruqi.dao.entity;

/**
 * 微信接收者
 */
public class WechatMsgReceiverEntity {
    public long id;
    public String openid;
    public String nickname;
    public String remark;
    // 1:启用，0:停用
    public int userStatus;
    // public Timestamp createTime;
    // public Timestamp modifyTime;
}