package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.aspect.LogAnnotation;
import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.network.ErrorCode;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import com.ruqi.appserver.ruqi.service.EventService;
import com.ruqi.appserver.ruqi.service.IRecordService;
import com.ruqi.appserver.ruqi.utils.DotEventDataUtils;
import com.ruqi.appserver.ruqi.utils.IpUtil;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.util.List;
import java.util.Map;

/**
 * 记录埋点的控制器
 *
 * @author yich
 */
@RestController
@Api(tags = "记录埋点数据的入口")
@RequestMapping(value = "/recordV2")
public class RecordV2Controller extends BaseController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    EventService eventService;

    @Autowired
    IRecordService recordService;

    @ApiOperation(value = "应用设备风险上报", notes = "")
    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    public BaseCodeMsgBean BaseBean(@RequestBody RecordInfo<RiskInfo> content) {
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
                        if (points != null && points.size() > 0) {
                            firstPoints = JsonUtil.beanToJsonStr(points.get(0));
                        }
                        content.getContent().eventDetail = eventData.get(DotEventInfo.NAME_DRIVER_ID) + "_" + firstPoints;
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

}
