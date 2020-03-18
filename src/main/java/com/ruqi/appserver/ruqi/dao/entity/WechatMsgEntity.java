package com.ruqi.appserver.ruqi.dao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 微信消息发送记录
 */
@ApiModel
public class WechatMsgEntity {
    public long id;
    @ApiModelProperty(value = "每一条发送的微信消息的id")
    public String msgid;
    @ApiModelProperty(value = "消息接收者微信openid")
    public String openid;
    @ApiModelProperty(value = "消息详情内容")
    public String msgDetails;
    @ApiModelProperty(value = "备注信息")
    public String remark;
    @ApiModelProperty(value = "消息发送结果")
    public String result;
    @ApiModelProperty(value = "创建时间")
    public Date createTime;
    @ApiModelProperty(value = "变更时间")
    public Date modifyTime;
}