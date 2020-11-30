package com.ruqi.appserver.ruqi.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author ZhangYu
 * @date 2020/11/30
 * @desc 查询日统计推荐上车点埋点数据
 */
@ApiModel
public class QueryDayStaticRecPointDatasRequest extends BaseRequest {
    @NotBlank(message = "env不能为空")
    @Pattern(regexp = "prod|dev", message = "env必须为prod、dev之一")
    @ApiModelProperty(value = "环境类型，prod、dev")
    public String env;
}
