package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.bean.RiskInfo;
import com.ruqi.appserver.ruqi.service.IRecordService;
import com.ruqi.appserver.ruqi.utils.IpUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 记录埋点的控制器
 *
 * @author yich
 */
@RestController
@Api(tags = "记录埋点数据的入口")
@RequestMapping(value = "/record")
public class RecordController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    IRecordService recordService;
    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    public String saveData(@RequestBody RecordInfo<RiskInfo> content) {
        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        recordService.saveRecord(content,new Date(), IpUtil.getIpAddr(request));//通过异步操作，后期加上redis和队列保证并发不会出现问题
        return "OK";
    }
}
