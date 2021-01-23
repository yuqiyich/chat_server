package com.ruqi.appserver.ruqi.utils;

import com.ruqi.appserver.ruqi.bean.response.EventTypeKeyListResp;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author ZhangYu
 * @date 2020/10/16
 * @desc 埋点表的EventType、EventKey对应关系
 */
public class DotEventDataUtils {
    private static DotEventDataUtils instance;

    private EventTypeKeyListResp eventTypeKeyListResp;

    private DotEventDataUtils() {
    }

    public static DotEventDataUtils getInstance() { // 1
        if (instance == null) { // 2:第一次检查
            synchronized (DotEventDataUtils.class) { // 3:加锁
                if (instance == null) // 4:第二次检查
                    instance = new DotEventDataUtils(); // 5:问题的根源出在这里
            }
        }
        return instance;
    }

    // 拼接类型匹配的key的查询。
    public String getSqlStr(String eventType, EventTypeKeyListResp eventTypeKeyListResp) {
        if (null != eventTypeKeyListResp && !CollectionUtils.isEmpty(eventTypeKeyListResp.eventTypes)) {
            for (EventTypeKeyListResp.EventType type : eventTypeKeyListResp.eventTypes) {
                if (MyStringUtils.isEqueals(type.typeKey, eventType)) {
                    if (!CollectionUtils.isEmpty(type.eventKeys)) {
                        StringBuilder sb = new StringBuilder();
                        for (EventTypeKeyListResp.EventKey key : type.eventKeys) {
                            sb.append("event_key='" + key.eventKey + "' or ");
                        }
                        if (sb.length() > 4) {
                            return sb.substring(0, sb.length() - 4);
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }


    public void setEventTypeKeys(EventTypeKeyListResp eventTypeKeyListResp) {
        this.eventTypeKeyListResp = eventTypeKeyListResp;
    }

    public EventTypeKeyListResp getEventTypeKeys(boolean isOnlyValid) {
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
