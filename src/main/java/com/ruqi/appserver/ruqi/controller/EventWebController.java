package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.aspect.LogAnnotation;
import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.request.NewEventKeyRequest;
import com.ruqi.appserver.ruqi.bean.request.NewEventTypeRequest;
import com.ruqi.appserver.ruqi.bean.response.EventTypeKeyListResp;
import com.ruqi.appserver.ruqi.dao.entity.UserInfoEntity;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import com.ruqi.appserver.ruqi.service.EventService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "Web埋点系统接口")
@RequestMapping(value = "/web/event")
public class EventWebController extends BaseController {

    @Autowired
    EventService eventService;

    @ApiOperation(value = "埋点分类新增接口")
    @RequestMapping(value = "/v1/eventType/newType", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    @LogAnnotation
    public BaseBean newEventType(HttpServletRequest request,
                                 @Validated @RequestBody NewEventTypeRequest newEventTypeRequest,
                                 BindingResult bindingResult) {
        BaseBean result = checkRequestInvalid(bindingResult);
        if (null == result) {
            result = new BaseBean<>();
            // 已存在则提示已存在，否则新增
            if (eventService.isEventTypeExists(newEventTypeRequest.typeKey)) {
                result.errorCode = ErrorCodeMsg.ERROR_EXIETS_PARAMS.errorCode;
                result.errorMsg = "typeKey已存在，请检查";
            } else {
                UserInfoEntity userInfoEntity = (UserInfoEntity) request.getAttribute("userData");
                if (!eventService.saveEventType(newEventTypeRequest, userInfoEntity.id)) {
                    result.errorCode = ErrorCodeMsg.ERROR_SYSTEM.errorCode;
                    result.errorMsg = ErrorCodeMsg.ERROR_SYSTEM.errorMsg;
                }
            }
        }
        return result;
    }

    @ApiOperation(value = "埋点事件新增接口")
    @RequestMapping(value = "v1/eventKey/newKey", method = RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    @CrossOrigin
    public BaseBean newEventKey(HttpServletRequest request,
                                @Validated @RequestBody NewEventKeyRequest newEventKeyRequest, BindingResult bindingResult) {
        BaseBean<UserInfoEntity> result = checkRequestInvalid(bindingResult);
        if (null == result) {
            result = new BaseBean<>();
            // 已存在则提示已存在，否则新增
            if (!eventService.isEventTypeIdExists(newEventKeyRequest.typeId)) {
                result.errorCode = ErrorCodeMsg.ERROR_EXIETS_PARAMS.errorCode;
                result.errorMsg = "typeId不存在，请检查";
            } else if (eventService.isEventKeyExists(newEventKeyRequest.eventKey)) {
                result.errorCode = ErrorCodeMsg.ERROR_EXIETS_PARAMS.errorCode;
                result.errorMsg = "eventKey已存在，请检查";
            } else {
                UserInfoEntity userInfoEntity = (UserInfoEntity) request.getAttribute("userData");
                if (!eventService.saveEventKey(newEventKeyRequest, userInfoEntity.id)) {
                    result.errorCode = ErrorCodeMsg.ERROR_SYSTEM.errorCode;
                    result.errorMsg = ErrorCodeMsg.ERROR_SYSTEM.errorMsg;
                }
            }
        }
        return result;
    }

    @ApiOperation(value = "埋点分类事件查询接口")
    @RequestMapping(value = "v1/eventTypeKey/list", method = RequestMethod.POST)
    @ResponseBody
    @LogAnnotation
    @CrossOrigin
    public BaseBean<EventTypeKeyListResp> getEventTypeKeyList() {
        BaseBean<EventTypeKeyListResp> result = new BaseBean();
        result.data = eventService.getEventTypeKeys();
        return result;
    }
}
