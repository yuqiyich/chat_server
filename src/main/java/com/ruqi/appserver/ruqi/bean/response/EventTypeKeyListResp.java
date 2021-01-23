package com.ruqi.appserver.ruqi.bean.response;

import com.ruqi.appserver.ruqi.bean.dbbean.DBEventKey;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventType;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhangYu
 * @date 2020/11/24
 * @desc 埋点的类型、事件数据
 */
public class EventTypeKeyListResp {
    @ApiModelProperty(value = "类型")
    public List<EventType> eventTypes;

    public static class EventType {
        public long id;
        @ApiModelProperty(value = "")
        public String typeKey;
        @ApiModelProperty(value = "类型名")
        public String typeKeyName;
        @ApiModelProperty(value = "类型备注")
        public String remark;
        @ApiModelProperty(value = "状态 1-启用 0-禁用")
        public int status; // 这里暂时没处理状态是否启用，正常某些情况下接口可以控制只返回启用数据，则不需要返回带上该字段
        @ApiModelProperty(value = "创建人")
        public String createUserName;
        @ApiModelProperty(value = "创建时间")
        public String createTime;
        @ApiModelProperty(value = "类型下的事件")
        public List<EventKey> eventKeys;

        public boolean isStatusValid() {
            return 1 == status;
        }

        public static EventType convertFromDBEventType(DBEventType dbEventType) {
            EventType eventType = new EventType();
            if (null != dbEventType) {
                eventType.id = dbEventType.id;
                eventType.typeKey = dbEventType.typeKey;
                eventType.typeKeyName = dbEventType.typeKeyName;
                eventType.remark = dbEventType.remark;
                eventType.status = dbEventType.status;
                eventType.createUserName = dbEventType.createUserName;
                eventType.createTime = dbEventType.createTime;
                eventType.eventKeys = new ArrayList<>();
            }
            return eventType;
        }
    }

    public static class EventKey {
        public long id;
        @ApiModelProperty(value = "")
        public long typeId;
        @ApiModelProperty(value = "")
        public String eventKey;
        @ApiModelProperty(value = "事件名")
        public String eventKeyName;
        @ApiModelProperty(value = "事件备注")
        public String remark;
        @ApiModelProperty(value = "状态 1-启用 0-禁用")
        public int status;
        @ApiModelProperty(value = "创建人")
        public String createUserName;
        @ApiModelProperty(value = "创建时间")
        public String createTime;

        public boolean isStatusValid() {
            return 1 == status;
        }

        public static EventKey convertFromDBEventKey(DBEventKey dbEventKey) {
            EventKey eventKey = new EventKey();
            if (null != dbEventKey) {
                eventKey.id = dbEventKey.id;
                eventKey.typeId = dbEventKey.typeId;
                eventKey.eventKey = dbEventKey.eventKey;
                eventKey.eventKeyName = dbEventKey.eventKeyName;
                eventKey.remark = dbEventKey.remark;
                eventKey.status = dbEventKey.status;
                eventKey.createUserName = dbEventKey.createUserName;
                eventKey.createTime = dbEventKey.createTime;
            }
            return eventKey;
        }
    }
}

