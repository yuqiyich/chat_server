function getUserTypeValue(userType) {
    switch (userType) {
        case 1:
            return "APP通用";
        case 2:
            return "未登录用户";
        case 3:
            return "专车用户";
        case 4:
            return "顺风车乘客";
        case 5:
            return "顺风车车主";
        default:
            return "";
    }
}

function getEndPointEventKeyValue(eventKey) {
    switch (eventKey) {
        case "FB_SUC_DES_POP_SHOW":
            return "推荐下车点气泡展示兜底点";
        case "FB_SUC_DES_POP_SELECT":
            return "推荐下车点气泡兜底点被选中";
        case "FB_SUC_DES_POP_ORDER":
            return "推荐下车点气泡兜底点下单成功";
        case "FB_SUC_DES_LIST_SHOW":
            return "地址列表展示兜底点";
        case "FB_SUC_DES_LIST_SELECT":
            return "地址列表兜底点被选中";
        case "FB_SUC_DES_LIST_ORDER":
            return "地址列表都地点下单成功";
        default:
            return "";
    }
}

function getVoiceDialEventKeyValue(eventKey) {
    switch (eventKey) {
        case "VOICE_CALL_START_CALL":
            return "发起通话";
        case "VOICE_CALL_CALLER_CONNECT_SUCCESS":
            return "发起者连接成功";
        case "VOICE_CALL_CALLER_CONNECT_FAIL":
            return "发起者连接失败";
        case "VOICE_CALL_CALLER_SEND_MSG":
            return "发起者发送IM消息";
        case "VOICE_CALL_RECEIVER_GOT_MSG":
            return "接受者接收到IM消息";
        case "VOICE_CALL_RECEIVER_JOIN_CHANNEL":
            return "接受者加入频道";
        case "VOICE_CALL_RECEIVER_CONNECT_SUCCESS":
            return "接受者连接成功";
        case "VOICE_CALL_RECEIVER_CONNECT_FAIL":
            return "接收者连接失败";
        default:
            return "";
    }
}
