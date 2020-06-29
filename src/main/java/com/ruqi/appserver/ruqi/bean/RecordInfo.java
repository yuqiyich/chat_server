package com.ruqi.appserver.ruqi.bean;

/**
 * 纪律数据的基础bean
 *
 * @author yich
 */
//@Data
//@Document(indexName = "recordinfo")
public class RecordInfo<T extends BaseRecordInfo> {
    //    @Id  // id 是主键
//    @Field(type = FieldType.Keyword)
    private String id;
    private int page = 1;
    private int limit = 10;
    private AppInfo appInfo;//应用表
    private UserEntity userInfo;//用户信息表
    //    @Field(type = FieldType.Integer)
    private int RecordType = RecordTypeEnum.RUNTIME_RISK.getId();//默认记录类型
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

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RecordInfo{" +
                "id=" + id +
                ", page=" + page +
                ", limit=" + limit +
                ", appInfo=" + appInfo +
                ", userInfo=" + userInfo +
                ", RecordType=" + RecordType +
                ", content=" + content +
                '}';
    }
}
