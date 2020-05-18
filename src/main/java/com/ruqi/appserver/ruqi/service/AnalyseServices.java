package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.controller.WechatController;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import com.ruqi.appserver.ruqi.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Component
@EnableScheduling   // 1.开启定时任务
public class AnalyseServices {
    Logger logger = LoggerFactory.getLogger(getClass());

    private static final long INTERVAL = 1000 * 60 * 60;//上报时间间隔
    private static final long MAX_DRIVER_THRESHOLD_OF_DAY = 50;//司机端最大上报数
    private static final long MAX_CLIENT_THRESHOLD_OF_DAY = 50;//最大上报数
    private static final long APP_CLIENT = 2;//乘客端应用id
    private static final long APP_DRIVER = 1;//司机端应用id
    private static final String CRON_REG = "0 0 10 * * ? ";//


    @Autowired
    RiskInfoWrapper riskInfoWrapper;

    @Autowired
    AppInfoWrapper appInfoWrapper;

    @Autowired
    WechatController mWechatController;

    @Value("${spring.profiles.active}")
    private String mEnv = "";

    @Scheduled(cron = CRON_REG)
    public void periodCheckSecurity() throws InterruptedException {
        if ("prod".equals(mEnv)) {
            List<AppInfo> appInfos = appInfoWrapper.getAllApps();
            if (!CollectionUtils.isEmpty(appInfos)) {
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.getAppId() == APP_CLIENT || appInfo.getAppId() == APP_DRIVER) {
                        int count = riskInfoWrapper.countSecurityNum(appInfo.getAppId(), DateTimeUtils.getYesterdayStartDate(),  DateTimeUtils.getYesterdayEndDate());
                        if (count >= 0) {//不设阈值
                            logger.info("risk count:" + count);
                            mWechatController.sendSecurityTemplateMsg(appInfo.getAppName(), "设备风险",
                                    "在过去的"  + DateTimeUtils.getYesterday() +"一天内总共有"+ count + "条设备风险数据[" + mEnv + "],发送ip:" + IpUtil.getLocalIP(), "请至APP记录平台查看完整详细信息", null);
                        } else {
                            logger.info("appId[" + appInfo.getAppId() + "]periodCheckSecurity method run,find no risk");
                        }
                    }
                }
            }
        }
    }
}
