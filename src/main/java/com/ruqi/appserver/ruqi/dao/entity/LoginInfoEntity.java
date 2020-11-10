package com.ruqi.appserver.ruqi.dao.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel
public class LoginInfoEntity {
    @NotBlank(message = "account不允许为空")
    @ApiModelProperty(value = "账号", required = true)
    public String account;
    @NotBlank(message = "password不允许为空")
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
