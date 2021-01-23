package com.ruqi.appserver.ruqi.network;

public enum ErrorCodeMsg {

    ERROR_NO_USER(10001, "用户不存在或密码错误"),
    ERROR_INVALID_USER(10002, "用户已被禁用"),

    ERROR_INVALID_PARAMS(400, "参数校验失败"),
    ERROR_USER_TOKEN(401, "TOKEN无效，请重新登录"),
    ERROR_EXIETS_PARAMS(402, "参数已存在"),
    ERROR_NOT_EXIETS_PARAMS(403, "参数不存在，请检查"),
    ERROR_INVALID_EVENT_KEY(405, "该埋点类型上报已被禁用，记录上报保存失败"),

    ERROR_SYSTEM(500, "系统异常");

    public int errorCode;
    public String errorMsg;

    ErrorCodeMsg(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

}
