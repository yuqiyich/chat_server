package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.aspect.LogAnnotation;
import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.request.GaiaRecmdWeekDataRequest;
import com.ruqi.appserver.ruqi.bean.response.EventDataGaiaRecmd;
import com.ruqi.appserver.ruqi.bean.response.EventDayDataH5Hybrid;
import com.ruqi.appserver.ruqi.dao.entity.DeviceRiskOverviewEntity;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import com.ruqi.appserver.ruqi.service.EventService;
import com.ruqi.appserver.ruqi.service.IRecordService;
import com.ruqi.appserver.ruqi.utils.*;
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
import java.util.HashMap;
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
public class RecordController extends BaseController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    EventService eventService;

    @Autowired
    IRecordService recordService;
//    @Autowired
//    BaseElasticService baseElasticService;

//    RecordInfoDAO recordInfoDAO = new RecordInfoDAO();

    /**
     * 查询记录到数据的版本号名称(for web 的layui的table控件接口)
     *
     * @return
     */
    @ApiOperation(value = "查询记录到数据的风险类型", notes = "")
    @RequestMapping(value = "/queryRiskTypeForLayui", method = RequestMethod.GET)
    @ResponseBody
    @CrossOrigin
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
    @CrossOrigin
    public BaseBean<List<String>> queryAppVersionNameForLayui() {
        BaseBean<List<String>> result = new BaseBean<>();
        List<String> appVersionNameList = recordService.queryAppVersionNameForLayui();
        result.data = appVersionNameList;
        return result;
    }

//    @Resource
//    private ElasticsearchTemplate elasticsearchTemplate;

    @ApiOperation(value = "应用设备风险上报", notes = "")
    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    public BaseCodeMsgBean BaseBean(@RequestBody RecordInfo<RiskInfo> content) {
//        ElasticEntity elasticEntity = new ElasticEntity();
//        elasticEntity.setId("121");
//        Map map = new HashMap();
//        map.put("key1", "value11");
//        map.put("key2", "value222");
//        elasticEntity.setData(map);
//        baseElasticService.insertOrUpdateOne("idName1", elasticEntity);

//        TestDoc1 testDoc1 = new TestDoc1();
//        testDoc1.content = "content1";
//        testDoc1.desc = "desc1";
//
//        logger.info("--->uploadData");
//
//        String indexName = "zytest3";
//        logger.info("--->uploadData elasticsearchTemplate.indexExists(indexName)=" + elasticsearchTemplate.indexExists(indexName));
//        if (!elasticsearchTemplate.indexExists(indexName)) {
//            System.out.println(elasticsearchTemplate.createIndex(indexName));
//            System.out.println(elasticsearchTemplate.putMapping(TestDoc1.class));
//            elasticsearchTemplate.refresh(indexName);
//        }

//        System.out.println(elasticsearchTemplate.getClient().p(indexName));
//        System.out.println(elasticsearchTemplate.queryForAlias(indexName));
//        logger.info("--->uploadData elasticsearchTemplate.indexExists(TestDoc1.class)=" + elasticsearchTemplate.indexExists(TestDoc1.class));
//        if (elasticsearchTemplate.indexExists(TestDoc1.class)) {
//            System.out.println(elasticsearchTemplate.createIndex(TestDoc1.class));
//            System.out.println(elasticsearchTemplate.putMapping(TestDoc1.class));
//        }

//
//        String id = "121";
//        content.setId(id);
//        recordInfoDAO.save(content);
//        recordInfoDAO.findById(id);

//        return new BaseCodeMsgBean();
        return saveData(content);
    }

    @ApiOperation(value = "通用简单埋点事件统计上报", notes = "")
    @RequestMapping(value = "/uploadDotEventData", method = RequestMethod.POST)
    @LogAnnotation
    public BaseCodeMsgBean uploadDotEventData(HttpServletRequest request, @RequestBody UploadRecordInfo<UploadDotEventInfo> content) {
        try {
            BaseCodeMsgBean baseCodeMsgBean = saveDotData(content);
            return baseCodeMsgBean;
        } catch (Exception e) {
            BaseCodeMsgBean result = new BaseCodeMsgBean();
            result.errorCode = ErrorCode.ERROR_UNKNOWN.errorCode;
            result.errorMsg = ErrorCode.ERROR_UNKNOWN.errorMsg;
            logger.error("uploadDotEventData error. content:" + content + ", e:" + e);
            return result;
        }
    }

    private BaseCodeMsgBean saveDotData(UploadRecordInfo<UploadDotEventInfo> content) {
        BaseCodeMsgBean result = new BaseCodeMsgBean();

        // 如果被禁用埋点type或者key，直接返回错误
        if (null != content && null != content.getContent() && DotEventDataUtils.getInstance().isExistsAndInvalid(null, content.getContent().eventKey)) {
            result.errorCode = ErrorCodeMsg.ERROR_INVALID_EVENT_KEY.errorCode;
            result.errorMsg = ErrorCodeMsg.ERROR_INVALID_EVENT_KEY.errorMsg;
        } else {
            //获取RequestAttributes
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            //从获取RequestAttributes中获取HttpServletRequest的信息
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            if (null != content && null != content.getContent()) {
                if (null != content.getContent().eventData) {
                    Map<String, Object> eventData = content.getContent().eventData;
                    if (eventData.containsKey(DotEventInfo.NAME_USER_TYPE)) {
                        content.getContent().userType = (int) eventData.get(DotEventInfo.NAME_USER_TYPE);
                    }
                    if (eventData.containsKey(DotEventInfo.NAME_START_LNG)) {
                        content.getContent().startLng = (double) eventData.get(DotEventInfo.NAME_START_LNG);
                    }
                    if (eventData.containsKey(DotEventInfo.NAME_START_LAT)) {
                        content.getContent().startLat = (double) eventData.get(DotEventInfo.NAME_START_LAT);
                    }
                    if (eventData.containsKey(DotEventInfo.NAME_DRIVER_ID)) {
                        List points = (List) eventData.get("points");
                        String firstPoints = "";
                        if (points!=null && points.size()>0){
                            firstPoints =JsonUtil.beanToJsonStr(points.get(0));
                        }
                        content.getContent().eventDetail = eventData.get(DotEventInfo.NAME_DRIVER_ID)+"_"+firstPoints;
                    }
                }
                recordService.saveDotRecord(content, IpUtil.getIpAddr(request));//通过异步操作，后期加上redis和队列保证并发不会出现问题
            }
        }

        return result;
    }

    private BaseCodeMsgBean saveData(RecordInfo<? extends BaseRecordInfo> content) {
        BaseCodeMsgBean result = new BaseCodeMsgBean();

        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        if (null != content && null != content.getContent()) {
            recordService.saveRecord(content, IpUtil.getIpAddr(request));//通过异步操作，后期加上redis和队列保证并发不会出现问题
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
    @CrossOrigin
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
    @CrossOrigin
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
     * 通用查询埋点生效记录用户数
     *
     * @return
     */
    @ApiOperation(value = "通用查询埋点生效记录用户数", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<DotEventInfo>", name = "参数对象", value = "参数类型")
    })
    @RequestMapping(value = "/queryCommonEventUserCount", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public BaseMapBean queryCommonEventUserCount(@RequestBody RecordInfo<DotEventInfo> params) {
        BaseMapBean result = new BaseMapBean();
        try {
            long totalSize = recordService.queryEventTotalUserSize(params);
            result.data = new HashMap<>();
            result.data.put("size", totalSize);
        } catch (Exception e) {
            result.errorCode = ErrorCode.ERROR_UNKNOWN.errorCode;
            result.errorMsg = ErrorCode.ERROR_UNKNOWN.errorMsg;
            logger.error("queryEventUserCount error.params=" + params + ", e:" + e);
        }
        return result;
    }

    /**
     * 通用查询埋点记录订单数
     *
     * @return
     */
    @ApiOperation(value = "通用查询埋点记录订单数", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "RecordInfo<DotEventInfo>", name = "参数对象", value = "参数类型")
    })
    @RequestMapping(value = "/queryCommonEventOrderCount", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public BaseMapBean queryCommonEventOrderCount(@RequestBody RecordInfo<DotEventInfo> params) {
        BaseMapBean result = new BaseMapBean();
        try {
            long totalSize = recordService.queryEventTotalOrderSize(params);
            result.data = new HashMap<>();
            result.data.put("size", totalSize);
        } catch (Exception e) {
            result.errorCode = ErrorCode.ERROR_UNKNOWN.errorCode;
            result.errorMsg = ErrorCode.ERROR_UNKNOWN.errorMsg;
            logger.error("queryEventOrderCount error. params:" + params + ", e:" + e);
        }
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
    @CrossOrigin
    public BaseBean<BasePageBean<RecordDotEventInfo>> queryCommonEventListForLayui(@RequestBody RecordInfo<DotEventInfo> params) {
        logger.info("queryCommonEventListForLayui params:" + params);
        BaseBean<BasePageBean<RecordDotEventInfo>> result = new BaseBean<>();
        List<RecordDotEventInfo> receiverEntities = recordService.queryCommonEventListForLayui(params.getPage() - 1, params.getLimit(), params);
        long totalSize = recordService.queryTotalSizeCommonEvent(params);
        result.data = new BasePageBean<>(params.getPage() - 1, params.getLimit(), totalSize, receiverEntities);
        return result;
    }

    /**
     * H5内嵌加载7天内每天每个事件总量
     *
     * @return
     */
    @ApiOperation(value = "H5内嵌加载7天内每天每个事件总量", notes = "")
    @RequestMapping(value = "/queryWeekDataH5Hybrid", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public BaseBean<List<EventDayDataH5Hybrid>> queryWeekDataH5Hybrid() {
        logger.info("queryWeekDataH5Hybrid");
        BaseBean<List<EventDayDataH5Hybrid>> result = new BaseBean<>();
        List<EventDayDataH5Hybrid> receiverEntities = recordService.queryWeekDataH5Hybrid();
        result.data = receiverEntities;
        return result;
    }

    /**
     * 推荐上车点盖亚兜底7天内数据总量
     *
     * @return
     */
    @ApiOperation(value = "推荐上车点盖亚兜底7天内数据总量", notes = "")
    @RequestMapping(value = "/queryWeekDataGaiaRecmd", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public BaseBean<List<EventDataGaiaRecmd>> queryWeekDataGaiaRecmd(@RequestBody GaiaRecmdWeekDataRequest request) {
        logger.info("queryWeekDataGaiaRecmd");
        BaseBean<List<EventDataGaiaRecmd>> result = new BaseBean<>();
        List<EventDataGaiaRecmd> receiverEntities = recordService.queryWeekDataGaiaRecmd(request.appId);
        result.data = receiverEntities;
        return result;
    }

}
