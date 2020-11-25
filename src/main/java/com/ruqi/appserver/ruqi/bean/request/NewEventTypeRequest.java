package com.ruqi.appserver.ruqi.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@ApiModel
public class NewEventTypeRequest {
    @NotBlank(message = "key不允许为空")
    @Length(max = 20, message = "key长度不能超过20")
    @ApiModelProperty(value = "typeKey", required = true)
    public String typeKey;
    @Length(max = 20, message = "名长度不能超过20")
    @ApiModelProperty(value = "名")
    public String typeKeyName;
    @Length(max = 255, message = "备注长度不能超过255")
    @ApiModelProperty(value = "备注")
    public String remark;

    @Override
    public String toString() {
        return "NewEventTypeRequest{" +
                "typeKey='" + typeKey + '\'' +
                ", typeKeyName='" + typeKeyName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
