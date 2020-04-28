package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.*;
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

    /**
     * 查询记录到数据的版本号名称(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询记录到数据的风险类型", notes = "")
    @RequestMapping(value = "/queryRiskTypeForLayui", method = RequestMethod.GET)
    @ResponseBody
    public BaseBean<List<String>> queryRiskTypeForLayui() {
        BaseBean<List<String>> result = new BaseBean<>();
        List<String> riskTypeListList = recordService.queryRiskTypeForLayui();
        result.data = riskTypeListList;
        return result;
    }

    /**
     * 查询记录到数据的版本号名称(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询记录到数据的版本号名称", notes = "")
    @RequestMapping(value = "/queryAppVersionNameForLayui", method = RequestMethod.GET)
    @ResponseBody
    public BaseBean<List<String>> queryAppVersionNameForLayui() {
        BaseBean<List<String>> result = new BaseBean<>();
        List<String> appVersionNameList = recordService.queryAppVersionNameForLayui();
        result.data = appVersionNameList;
        return result;
    }

    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    public String saveData(@RequestBody RecordInfo<RiskInfo> content) {
        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        recordService.saveRecord(content, new Date(), IpUtil.getIpAddr(request));//通过异步操作，后期加上redis和队列保证并发不会出现问题
        return "OK";
    }


    /**
     * 查询报警列表
     *
     * @return
     */
    @ApiOperation(value = "查询报警列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<RiskInfo>", name = "参数对象", value = "参数类型", required = false)
    })
    @RequestMapping(value = "/queryRiskList", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<RecordInfo<RiskInfo>>> getRiskInfoList(@RequestBody RecordInfo<RiskInfo> params) {
        BaseBean<BasePageBean<RecordInfo<RiskInfo>>> result = new BaseBean<>();
        List<RecordInfo<RiskInfo>> receiverEntities = recordService.queryList(params.getPage() - 1, params.getLimit(), params);
        long totalSize = recordService.queryTotalSize(params);
        result.data = new BasePageBean<RecordInfo<RiskInfo>>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);
        return result;
    }

    /**
     * 查询报警列表(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询报警列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<RiskInfo>", name = "参数对象", value = "参数类型", required = false)
    })
    @RequestMapping(value = "/queryRiskListForLayui", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<RecordRiskInfo>> getRiskInfoListForLayui(@RequestBody RecordInfo<RiskInfo> params) {
        BaseBean<BasePageBean<RecordRiskInfo>> result = new BaseBean<>();
        List<RecordRiskInfo> receiverEntities = recordService.queryListForLayUi(params.getPage() - 1, params.getLimit(), params);
        long totalSize = recordService.queryTotalSize(params);
        result.data = new BasePageBean<RecordRiskInfo>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);

        return result;
    }

}
