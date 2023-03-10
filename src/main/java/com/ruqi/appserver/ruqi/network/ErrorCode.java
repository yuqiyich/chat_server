package com.ruqi.appserver.ruqi.network;

public enum ErrorCode {
    ERROR_UNKNOWN(808, "未知错误"),
    ERROR_SYSTEM(500, "系统异常，请重试");

    public String errorMsg;
    public int errorCode;

    ErrorCode(int code, String msg) {
        errorCode = code;
        errorMsg = msg;
    }

    interface WeChatCode {
        // 获取 access_token 时 AppSecret 错误，或者 access_token 无效。请开发者认真比对 AppSecret 的正确性，或查看是否正在为恰当的公众号调用接口
        int CODE_TOKEN_INVALID = 40001;
        // access_token 超时，请检查 access_token 的有效期，请参考基础支持 - 获取 access_token 中，对 access_token 的详细机制说明
        int CODE_TOKEN_EXPIRED = 42001;
    }
}