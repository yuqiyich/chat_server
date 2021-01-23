package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.dbbean.DBEventKey;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventType;
import com.ruqi.appserver.ruqi.bean.request.NewEventKeyRequest;
import com.ruqi.appserver.ruqi.bean.request.NewEventTypeRequest;
import com.ruqi.appserver.ruqi.bean.response.EventTypeKeyListResp;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.request.ModifyEventStatusRequest;
import com.ruqi.appserver.ruqi.utils.DotEventDataUtils;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangYu
 * @date 2020/11/24
 * @desc 埋点服务
 */
@Service
public class EventService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    DotEventInfoWrapper dotEventInfoWrapper;

    public boolean saveEventType(NewEventTypeRequest request, long userId) {
        if (null != request && !MyStringUtils.isEmpty(request.typeKey)) {
            try {
                if (1 == dotEventInfoWrapper.insertDotEventType(request, userId)) {
                    refreshEventTypeKeyListResp();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean saveEventKey(NewEventKeyRequest newEventKeyRequest, long userId) {
        if (null != newEventKeyRequest && 0 < newEventKeyRequest.typeId
                && !MyStringUtils.isEmpty(newEventKeyRequest.eventKey)) {
            try {
                if (1 == dotEventInfoWrapper.insertDotEventKey(newEventKeyRequest, userId)) {
                    refreshEventTypeKeyListResp();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean modifyStatus(ModifyEventStatusRequest modifyEventStatusRequest) {
        if (null != modifyEventStatusRequest) {
            try {
                if (1 == dotEventInfoWrapper.updateEventStatus(modifyEventStatusRequest.level,
                        modifyEventStatusRequest.name, modifyEventStatusRequest.status)) {
                    refreshEventTypeKeyListResp();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void refreshEventTypeKeyListResp() {
        EventTypeKeyListResp mEventTypeKeyListResp = new EventTypeKeyListResp();
        List<EventTypeKeyListResp.EventType> eventTypeList = new ArrayList<>();
        List<DBEventType> dbEventTypeList = dotEventInfoWrapper.getEventTypes();
        List<DBEventKey> dbEventKeyList = dotEventInfoWrapper.getEventKeys();
        // 处理类型
        for (DBEventType dbEventType : dbEventTypeList) {
            EventTypeKeyListResp.EventType eventType = EventTypeKeyListResp.EventType.convertFromDBEventType(dbEventType);
            eventTypeList.add(eventType);
        }
        // 处理事件
        for (DBEventKey dbEventKey : dbEventKeyList) {
            for (EventTypeKeyListResp.EventType eventType : eventTypeList) {
                if (eventType.id == dbEventKey.typeId) {
                    EventTypeKeyListResp.EventKey eventKey = EventTypeKeyListResp.EventKey.convertFromDBEventKey(dbEventKey);
                    eventType.eventKeys.add(eventKey);
                    break;
                }
            }
        }
        mEventTypeKeyListResp.eventTypes = eventTypeList;
        DotEventDataUtils.getInstance().setEventTypeKeys(mEventTypeKeyListResp);
    }
}
