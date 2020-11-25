package com.ruqi.appserver.ruqi.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@ApiModel
public class NewEventKeyRequest {
    @Min(value = 1, message = "typeId错误")
    @ApiModelProperty(value = "typeId", required = true)
    public long typeId;
    @NotBlank(message = "eventKey不允许为空")
    @Length(max = 50, message = "eventKey长度不能超过50")
    @ApiModelProperty(value = "eventKey", required = true)
    public String eventKey;
    @Length(max = 20, message = "名长度不能超过20")
    @ApiModelProperty(value = "名")
    public String eventKeyName;
    @Length(max = 255, message = "备注长度不能超过255")
    @ApiModelProperty(value = "备注")
    public String remark;

    @Override
    public String toString() {
        return "NewEventTypeRequest{" +
                "typeId=" + typeId +
                ", eventKey='" + eventKey + '\'' +
                ", eventKeyName='" + eventKeyName + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
