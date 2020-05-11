package com.ruqi.appserver.ruqi.bean;

public enum RecordTypeEnum {
    // 设备安全相关
    RUNTIME_RISK("device_runtime_risk", 1, 0),
    // 乘客端 推荐点失败降级生效记录
    RECOMMEND_POINT_RISK("recommend_point_risk", 1001, 0);


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private int id;
    private String name;
    private int level;

    RecordTypeEnum(String name, int id, int level) {
        this.name = name;
        this.id = id;
        this.level = level;
    }
}
