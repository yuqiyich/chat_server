package com.ruqi.appserver.ruqi.bean;

/**
 * 纪律数据的基础bean
 *
 * @author yich
 */
public class RecordInfo<T extends RiskInfo> {
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    private int page=1;
    private int limit=10;
    private AppInfo appInfo;//应用表
    private UserEntity userInfo;//用户信息表
    private int RecordType = RiskEnum.RUNTIME_RISK.getId();//默认记录类型
    private T content;//核心数据字段

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public UserEntity getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserEntity userInfo) {
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
                "page=" + page +
                ", limit=" + limit +
                ", appInfo=" + appInfo +
                ", userInfo=" + userInfo +
                ", RecordType=" + RecordType +
                ", content=" + content +
                '}';
    }
}
