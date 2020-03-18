package com.ruqi.appserver.ruqi.dao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 微信接收者
 */
@ApiModel
public class WechatMsgReceiverEntity {
    public long id;
    @ApiModelProperty(value = "微信openId")
    public String openid;
    @ApiModelProperty(value = "微信昵称")
    public String nickname;
    @ApiModelProperty(value = "备注名")
    public String remark;
    @ApiModelProperty(value = "启用状态 1:启用，0:停用")
    public int userStatus;
    // public Date createTime;
    // public Date modifyTime;
}