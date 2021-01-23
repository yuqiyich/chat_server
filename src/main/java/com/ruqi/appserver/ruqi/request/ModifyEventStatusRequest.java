package com.ruqi.appserver.ruqi.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author ZhangYu
 * @date 2021/1/21
 * @desc 更改埋点事件类型的启用状态
 */
@ApiModel
public class ModifyEventStatusRequest extends BaseRequest {
    @NotBlank(message = "level不能为空")
    @Pattern(regexp = "type|key", message = "level有误")
    @ApiModelProperty(value = "级别，type：类型，key：事件")
    public String level;

    @NotBlank(message = "name不能为空")
    @ApiModelProperty(value = "类型或事件名")
    public String name;

    @DecimalMax(value = "1")
    @DecimalMin(value = "0", message = "status 状态，1：启用，0：禁用")
    @ApiModelProperty(value = "状态，1：启用，0：禁用")
    public int status;
}
