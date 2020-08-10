package com.ruqi.appserver.ruqi.bean;


import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ZhangYu
 * @date 2020/5/11
 * @desc 设备风险总览
 */
public class RiskOverviewInfo extends BaseRecordInfo {
    public static final String TYPE_RISK_TYPE = "riskType";
    public static final String TYPE_APP_VERSION = "appVersion";
    public static final String TYPE_DEVICE_MODEL = "deviceModel";
    public static final String TYPE_DEVICE_BRAND = "deviceBrand";
    public static final String TYPE_PHONE_NUM = "phoneNum";
    public static final String TYPE_DEVICE_ID = "deviceID";
    public static final String TYPE_ANDROID_VERSION = "androidVersion";

    public String overviewType; // 统计类别
    /*****
     *查询字段参数
     */
    public String duringTime;
    public Date startDate;
    public Date endDate;

    public String getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(String duringTime) {
        this.duringTime = duringTime;
        createStartEndDate();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void createStartEndDate() {
        if (!StringUtils.isEmpty(duringTime)) {
            String[] str = duringTime.split("~");
            if (str.length == 2) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    startDate = format.parse(str[0].trim());
                    endDate = format.parse(str[1].trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*****
     *查询字段参数
     */
}
