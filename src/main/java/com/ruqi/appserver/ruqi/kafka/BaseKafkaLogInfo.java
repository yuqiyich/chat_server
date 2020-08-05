package com.ruqi.appserver.ruqi.kafka;

public class BaseKafkaLogInfo {

    public String level;
    public String message;

    public BaseKafkaLogInfo(LogLevel level, String message) {
        this.level = level.value;
        this.message = message;
    }

    public enum LogLevel {
        DEBUG("DEBUG"), INFO("INFO"), WARN("WARN"), ERROR("ERROR");

        public String value;

        LogLevel(String value) {
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return "BaseKafkaLogInfo{" +
                "level='" + level + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
