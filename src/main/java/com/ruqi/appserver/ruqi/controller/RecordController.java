package com.ruqi.appserver.ruqi.controller;

import com.google.gson.Gson;
import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.dao.entity.DeviceRiskOverviewEntity;
import com.ruqi.appserver.ruqi.service.IRecordService;
import com.ruqi.appserver.ruqi.utils.EncryptUtils;
import com.ruqi.appserver.ruqi.utils.IpUtil;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
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
import java.util.Map;

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

    @ApiOperation(value = "应用设备风险上报", notes = "")
    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    public BaseCodeMsgBean BaseBean(@RequestBody RecordInfo<RiskInfo> content) {
        return saveData(content);
    }

    @ApiOperation(value = "通用简单埋点事件统计上报", notes = "")
    @RequestMapping(value = "/uploadDotEventData", method = RequestMethod.POST)
    public BaseCodeMsgBean uploadDotEventData(@RequestBody RecordInfo<DotEventInfo> content) {
        return saveData(content);
    }

    private BaseCodeMsgBean saveData(RecordInfo<? extends BaseRecordInfo> content) {
        BaseCodeMsgBean result = new BaseCodeMsgBean();

        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (null != content && null != content.getContent()) {
            recordService.saveRecord(content, new Date(), IpUtil.getIpAddr(request));//通过异步操作，后期加上redis和队列保证并发不会出现问题
        }

        return result;
    }


    /**
     * 查询报警列表
     *
     * @return
     */
//    @ApiOperation(value = "查询报警列表", notes = "")
//    @ApiImplicitParams({
//            @ApiImplicitParam(dataType = "RecordInfo<RiskInfo>", name = "参数对象", value = "参数类型", required = false)
//    })
//    @RequestMapping(value = "/queryRiskList", method = RequestMethod.POST)
//    @ResponseBody
//    public BaseBean<BasePageBean<RecordInfo<RiskInfo>>> getRiskInfoList(@RequestBody RecordInfo<RiskInfo> params) {
//        BaseBean<BasePageBean<RecordInfo<RiskInfo>>> result = new BaseBean<>();
//        List<RecordInfo<RiskInfo>> receiverEntities = recordService.queryList(params.getPage() - 1, params.getLimit(), params);
//        long totalSize = recordService.queryTotalSize(params);
//        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);
//        return result;
//    }

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
        if (null != params && null != params.getUserInfo() && !MyStringUtils.isEmpty(params.getUserInfo().userPhone)) {
            params.getUserInfo().userPhone = EncryptUtils.encode(params.getUserInfo().userPhone);
        }
        List<RecordRiskInfo> receiverEntities = recordService.queryListForLayUi(params.getPage() - 1, params.getLimit(), params);
        if (null != receiverEntities) {
            for (RecordRiskInfo recordRiskInfo : receiverEntities) {
                if (null != recordRiskInfo && !MyStringUtils.isEmpty(recordRiskInfo.userPhone)) {
                    recordRiskInfo.userPhone = EncryptUtils.decode(recordRiskInfo.userPhone);
                    if (recordRiskInfo.userPhone.length() == 11) {
                        recordRiskInfo.userPhone = recordRiskInfo.userPhone.substring(0, 3) + "****" + recordRiskInfo.userPhone.substring(7, 11);
                    }
                }
            }
        }
        long totalSize = recordService.queryTotalSize(params);
        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);

        return result;
    }

    /**
     * 查询设备风险总览列表(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询设备风险总览列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<RiskOverviewInfo>", name = "参数对象", value = "参数类型", required = true)
    })
    @RequestMapping(value = "/queryRiskOverview", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<DeviceRiskOverviewEntity>> queryRiskOverview(@RequestBody RecordInfo<RiskOverviewInfo> params) {
        BaseBean<BasePageBean<DeviceRiskOverviewEntity>> result = new BaseBean<>();
        List<DeviceRiskOverviewEntity> deviceRiskOverviewEntityList = recordService.queryOverviewList(params.getPage() - 1, params.getLimit(), params);
        if (null != deviceRiskOverviewEntityList) {
            for (DeviceRiskOverviewEntity deviceRiskOverviewEntity : deviceRiskOverviewEntityList) {
                if (null != deviceRiskOverviewEntity && !MyStringUtils.isEmpty(deviceRiskOverviewEntity.userPhone)) {
                    deviceRiskOverviewEntity.userPhone = EncryptUtils.decode(deviceRiskOverviewEntity.userPhone);
                    if (deviceRiskOverviewEntity.userPhone.length() == 11) {
                        deviceRiskOverviewEntity.userPhone = deviceRiskOverviewEntity.userPhone.substring(0, 3) + "****" + deviceRiskOverviewEntity.userPhone.substring(7, 11);
                    }
                }
            }
        }
        long totalSize = recordService.queryOverviewTotalSize(params);
        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, deviceRiskOverviewEntityList);

        return result;
    }

    /**
     * 推荐点降级生效记录列表(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询推荐点降级生效记录列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<DotEventInfo>", name = "参数对象", value = "参数类型")
    })
    @RequestMapping(value = "/queryEventRecmdPointListForLayui", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<RecordDotEventInfo>> queryEventRecmdPointListForLayui(@RequestBody RecordInfo<DotEventInfo> params) {
        String eventType = "recmdPoint";
        BaseBean<BasePageBean<RecordDotEventInfo>> result = new BaseBean<>();
        List<RecordDotEventInfo> receiverEntities = recordService.queryCommonEventListForLayui(params.getPage() - 1, params.getLimit(), params, eventType);
        long totalSize = recordService.queryTotalSizeCommonEvent(params, eventType);
        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);

        return result;
    }

    /**
     * 算路降级生效记录列表(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询算路降级生效记录列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<DotEventInfo>", name = "参数对象", value = "参数类型")
    })
    @RequestMapping(value = "/queryEventNavListForLayui", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<RecordDotEventInfo>> queryEventNavListForLayui(@RequestBody RecordInfo<DotEventInfo> params) {
        String eventType = "nav";
        BaseBean<BasePageBean<RecordDotEventInfo>> result = new BaseBean<>();
        List<RecordDotEventInfo> receiverEntities = recordService.queryCommonEventListForLayui(params.getPage() - 1, params.getLimit(), params, eventType);
        long totalSize = recordService.queryTotalSizeCommonEvent(params, eventType);
        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);

        return result;
    }

    /**
     * 司机定位缓存点生效记录列表(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询司机定位缓存点生效记录", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<DotEventInfo>", name = "参数对象", value = "参数类型")
    })
    @RequestMapping(value = "/queryEventDriverLocationListForLayui", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<RecordDotEventInfo>> queryEventDriverLocationListForLayui(@RequestBody RecordInfo<DotEventInfo> params) {
        String eventType = "driverLocation";
        BaseBean<BasePageBean<RecordDotEventInfo>> result = new BaseBean<>();
        List<RecordDotEventInfo> receiverEntities = recordService.queryCommonEventListForLayui(params.getPage() - 1, params.getLimit(), params, eventType);
        long totalSize = recordService.queryTotalSizeCommonEvent(params, eventType);
        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);

        return result;
    }

    /**
     * 通用数据埋点记录列表(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "通用数据埋点记录列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<DotEventInfo>", name = "参数对象", value = "参数类型")
    })
    @RequestMapping(value = "/queryCommonEventListForLayui", method = RequestMethod.POST)
    @ResponseBody
    public BaseBean<BasePageBean<RecordDotEventInfo>> queryCommonEventListForLayui(@RequestBody RecordInfo<DotEventInfo> params) {
        String eventType = "";
        BaseBean<BasePageBean<RecordDotEventInfo>> result = new BaseBean<>();
        List<RecordDotEventInfo> receiverEntities = recordService.queryCommonEventListForLayui(params.getPage() - 1, params.getLimit(), params, eventType);
        long totalSize = recordService.queryTotalSizeCommonEvent(params, eventType);
        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);

        return result;
    }

}
