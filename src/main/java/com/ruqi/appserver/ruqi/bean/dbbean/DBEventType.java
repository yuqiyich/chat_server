package com.ruqi.appserver.ruqi.bean.dbbean;

/**
 * @author ZhangYu
 * @date 2020/11/24
 * @desc 埋点类型
 */
public class DBEventType {
    public long id;
    public String typeKey;
    public String typeKeyName;
    public String remark;
    public int status;
    public String createUserName;
    public String createTime;

    public boolean isStatusEnable() {
        return 1 == status;
    }
}
