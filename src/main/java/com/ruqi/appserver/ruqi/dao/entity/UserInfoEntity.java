package com.ruqi.appserver.ruqi.dao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class UserInfoEntity {
    public long id;
    @ApiModelProperty(value = "账号")
    public String account;
    @ApiModelProperty(value = "昵称")
    public String nickname;
    @ApiModelProperty(value = "密码")
    public String password;
    @ApiModelProperty(value = "状态 1：启用，0：禁用")
    public String userStatus;
    @ApiModelProperty(value = "token")
    public String token;

    public boolean isValid() {
        return "1".equals(userStatus);
    }
}
