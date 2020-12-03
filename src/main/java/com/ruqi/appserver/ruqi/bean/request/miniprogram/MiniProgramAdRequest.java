package com.ruqi.appserver.ruqi.bean.request.miniprogram;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel
public class MiniProgramAdRequest {
    @NotBlank(message = "type不允许为空")
    @ApiModelProperty(value = "type", required = true)
    public String type;

}
