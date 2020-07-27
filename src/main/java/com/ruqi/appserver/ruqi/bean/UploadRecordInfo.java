package com.ruqi.appserver.ruqi.bean;

/**
 * 接口上传类定义
 *
 * @author yich
 */
public class UploadRecordInfo<T extends BaseUploadRecordInfo> {
    private UploadAppInfo appInfo;//应用表
    private UploadUserEntity userInfo;//用户信息表
    private int RecordType = RecordTypeEnum.RUNTIME_RISK.getId();//默认记录类型
    private T content;//核心数据字段

    public UploadAppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(UploadAppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public UploadUserEntity getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UploadUserEntity userInfo) {
        this.userInfo = userInfo;
    }

    public int getRecordType() {
        return RecordType;
    }

    public void setRecordType(int recordType) {
        RecordType = recordType;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RecordInfo{" +
                ", appInfo=" + appInfo +
                ", userInfo=" + userInfo +
                ", RecordType=" + RecordType +
                ", content=" + content +
                '}';
    }
}
