package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.BasePageBean;
import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.bean.RiskInfo;
import com.ruqi.appserver.ruqi.dao.entity.WechatMsgReceiverEntity;
import com.ruqi.appserver.ruqi.service.IRecordService;
import com.ruqi.appserver.ruqi.utils.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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


    /**
     * 查询获取微信公众号消息接收者列表
     *
     * @return
     */
    @ApiOperation(value = "查询报警列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "page", value = "页码，从1开始", required = false)
            , @ApiImplicitParam(dataType = "Integer", name = "limit", value = "size，如10", required = false)
            , @ApiImplicitParam(dataType = "RecordInfo<RiskInfo>", name = "参数对象", value = "参数类型", required = false)
    })
    @RequestMapping(value = "/queryRiskList", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<RecordInfo<RiskInfo>>> getRiskInfoList(@RequestParam(defaultValue = "1") Integer page,
                                                                           @RequestParam(defaultValue = "10") Integer limit,@RequestBody RecordInfo<RiskInfo> params) {
        BaseBean<BasePageBean<RecordInfo<RiskInfo>>> result = new BaseBean<>();
        List<RecordInfo<RiskInfo>> receiverEntities = recordService.queryList(page - 1, limit, params);
        long totalSize = recordService.queryTotalSize(params);
        result.data = new BasePageBean<RecordInfo<RiskInfo>>(page, limit, totalSize, receiverEntities);
        return result;
    }
}
