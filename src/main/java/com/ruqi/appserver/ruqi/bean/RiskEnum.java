package com.ruqi.appserver.ruqi.bean;

public enum RiskEnum {
    RUNTIME_RISK("device_runtime_risk", 1, 0);


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

    RiskEnum(String name, int id, int level) {
        this.name = name;
        this.id = id;
        this.level = level;
    }
}
