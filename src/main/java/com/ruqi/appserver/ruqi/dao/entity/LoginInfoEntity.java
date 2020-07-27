package com.ruqi.appserver.ruqi.dao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class LoginInfoEntity {
    @ApiModelProperty(value = "账号", required = true)
    public String account;
    @ApiModelProperty(value = "密码", required = true)
    public String password;

    @Override
    public String toString() {
        return "LoginInfoEntity{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
