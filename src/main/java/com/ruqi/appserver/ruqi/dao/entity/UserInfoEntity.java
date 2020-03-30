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
    // 理论上这个token不需要保存在数据库，应该是根据规则生成保存在redis中。暂时先不改动了。
    @ApiModelProperty(value = "token")
    public String token;

    public boolean userIsValid() {
        return "1".equals(userStatus);
    }
}
