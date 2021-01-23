package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.dbbean.DBEventKey;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventType;
import com.ruqi.appserver.ruqi.bean.request.NewEventKeyRequest;
import com.ruqi.appserver.ruqi.bean.request.NewEventTypeRequest;
import com.ruqi.appserver.ruqi.bean.response.EventTypeKeyListResp;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.request.ModifyEventStatusRequest;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    private EventTypeKeyListResp mEventTypeKeyListResp;

    public boolean saveEventType(NewEventTypeRequest request, long userId) {
        if (null != request && !MyStringUtils.isEmpty(request.typeKey)) {
            try {
                if (1 == dotEventInfoWrapper.insertDotEventType(request, userId)) {
                    mEventTypeKeyListResp = null; // 新增数据后清除本地缓存数据，则下次使用数据时会读取数据库
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
                    mEventTypeKeyListResp = null; // 新增数据后清除本地缓存数据，则下次使用数据时会读取数据库
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // 本地有数据则直接使用，本地数据为空则请求数据库。分布式情况下该方案会存在问题，则应该存到redis中且维护更新而非本地内存中。
    private EventTypeKeyListResp getEventTypeKeys() {
        if (null == mEventTypeKeyListResp || CollectionUtils.isEmpty(mEventTypeKeyListResp.eventTypes)) {
            mEventTypeKeyListResp = new EventTypeKeyListResp();
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
        }
        return mEventTypeKeyListResp;
    }

    public EventTypeKeyListResp getEventTypeKeys(boolean isOnlyValid) {
        EventTypeKeyListResp eventTypeKeyListResp = getEventTypeKeys();
        if (isOnlyValid && null != eventTypeKeyListResp && !CollectionUtils.isEmpty(eventTypeKeyListResp.eventTypes)) {
            for (int i = eventTypeKeyListResp.eventTypes.size() - 1; i >= 0; i--) {
                if (null == eventTypeKeyListResp.eventTypes.get(i) || !eventTypeKeyListResp.eventTypes.get(i).isStatusValid()) {
                    eventTypeKeyListResp.eventTypes.remove(i);
                } else if (!CollectionUtils.isEmpty(eventTypeKeyListResp.eventTypes.get(i).eventKeys)) {
                    List<EventTypeKeyListResp.EventKey> eventKeys = eventTypeKeyListResp.eventTypes.get(i).eventKeys;
                    for (int j = eventKeys.size() - 1; j >= 0; j--) {
                        if (null == eventKeys.get(j) || !eventKeys.get(j).isStatusValid()) {
                            eventKeys.remove(j);
                        }
                    }
                }
            }
        }
        return eventTypeKeyListResp;
    }

    public boolean isEventTypeExists(String typeKey) {
        boolean isExists = false;
        EventTypeKeyListResp eventTypeKeyListResp = getEventTypeKeys(false);
        if (null != eventTypeKeyListResp.eventTypes) {
            for (EventTypeKeyListResp.EventType eventType : eventTypeKeyListResp.eventTypes) {
                if (MyStringUtils.isEqueals(eventType.typeKey, typeKey)) {
                    isExists = true;
                    break;
                }
            }
        }
        return isExists;
    }

    public boolean isEventTypeIdExists(long typeId) {
        boolean isExists = false;
        EventTypeKeyListResp eventTypeKeyListResp = getEventTypeKeys(false);
        if (null != eventTypeKeyListResp.eventTypes) {
            for (EventTypeKeyListResp.EventType eventType : eventTypeKeyListResp.eventTypes) {
                if (eventType.id == typeId) {
                    isExists = true;
                    break;
                }
            }
        }
        return isExists;
    }

    public boolean isEventKeyExists(String eventKey) {
        boolean isExists = false;
        EventTypeKeyListResp eventTypeKeyListResp = getEventTypeKeys(false);
        if (null != eventTypeKeyListResp.eventTypes) {
            for (EventTypeKeyListResp.EventType eventType : eventTypeKeyListResp.eventTypes) {
                if (null != eventType.eventKeys) {
                    for (EventTypeKeyListResp.EventKey eventKeyBean : eventType.eventKeys) {
                        if (MyStringUtils.isEqueals(eventKeyBean.eventKey, eventKey)) {
                            isExists = true;
                            break;
                        }
                    }
                }
                if (isExists) {
                    break;
                }
            }
        }
        return isExists;
    }

    public boolean isEventTypeExistsAndInvalid(String typeKey) {
        if (MyStringUtils.isEmpty(typeKey)) {
            return false;
        }
        boolean isExistsAndInvalid = false;
        EventTypeKeyListResp eventTypeKeyListResp = getEventTypeKeys(false);
        if (null != eventTypeKeyListResp.eventTypes) {
            for (EventTypeKeyListResp.EventType eventType : eventTypeKeyListResp.eventTypes) {
                if (MyStringUtils.isEqueals(eventType.typeKey, typeKey)) {
                    isExistsAndInvalid = !eventType.isStatusValid();
                    break;
                }
            }
        }
        return isExistsAndInvalid;
    }

    // key是否存在且禁用状态
    public boolean isEventKeyExistsAndInvalid(String eventKey) {
        if (MyStringUtils.isEmpty(eventKey)) {
            return false;
        }
        boolean isExists = false;
        boolean isExistsAndInvalid = false;
        EventTypeKeyListResp eventTypeKeyListResp = getEventTypeKeys(false);
        if (null != eventTypeKeyListResp.eventTypes) {
            for (EventTypeKeyListResp.EventType eventType : eventTypeKeyListResp.eventTypes) {
                if (null != eventType.eventKeys) {
                    for (EventTypeKeyListResp.EventKey eventKeyBean : eventType.eventKeys) {
                        if (MyStringUtils.isEqueals(eventKeyBean.eventKey, eventKey)) {
                            isExists = true;
                            isExistsAndInvalid = !eventKeyBean.isStatusValid();
                            break;
                        }
                    }
                }
                if (isExists) {
                    break;
                }
            }
        }
        return isExistsAndInvalid;
    }

    // key是否存在对应的type，且type是禁用状态
    public boolean isEventKeyTypeExistsAndInvalid(String eventKey) {
        if (MyStringUtils.isEmpty(eventKey)) {
            return false;
        }
        boolean isExists = false;
        boolean isExistsAndInvalid = false;
        EventTypeKeyListResp eventTypeKeyListResp = getEventTypeKeys(false);
        if (null != eventTypeKeyListResp.eventTypes) {
            for (EventTypeKeyListResp.EventType eventType : eventTypeKeyListResp.eventTypes) {
                if (null != eventType.eventKeys) {
                    for (EventTypeKeyListResp.EventKey eventKeyBean : eventType.eventKeys) {
                        if (MyStringUtils.isEqueals(eventKeyBean.eventKey, eventKey)) {
                            isExists = true;
                            isExistsAndInvalid = !eventType.isStatusValid();
                            break;
                        }
                    }
                }
                if (isExists) {
                    break;
                }
            }
        }
        return isExistsAndInvalid;
    }

    public boolean modifyStatus(ModifyEventStatusRequest modifyEventStatusRequest) {
        if (null != modifyEventStatusRequest) {
            try {
                if (1 == dotEventInfoWrapper.updateEventStatus(modifyEventStatusRequest.level,
                        modifyEventStatusRequest.name, modifyEventStatusRequest.status)) {
                    mEventTypeKeyListResp = null; // 新增数据后清除本地缓存数据，则下次使用数据时会读取数据库
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean isExistsAndInvalid(String eventType, String eventKey) {
        if (isEventKeyExistsAndInvalid(eventKey)) { // key是否存在且禁用
            return true;
        } else if (isEventTypeExistsAndInvalid(eventType)) { // type是否存在且禁用
            return true;
        } else if (isEventKeyTypeExistsAndInvalid(eventKey)) { // key对应的type是否存在且禁用
            return true;
        }
        return false;
    }
}
