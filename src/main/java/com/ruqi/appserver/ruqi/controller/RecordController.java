package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.service.IRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 记录埋点的控制器
 *
 * @author yich
 */
@RestController
@Api(value = "记录埋点数据的入口")
@RequestMapping(value = "/record")
public class RecordController {

    @Autowired
    IRecordService recordService;

    @ApiImplicitParams({@ApiImplicitParam(name = "content", value = "", required = true, dataType = "String")})
    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    public String saveData(@RequestBody RecordInfo content) {
        recordService.saveRecord(content,new Date());//通过异步操作，后期加上redis和队列保证并发不会出现问题
        return "OK";
    }
}
