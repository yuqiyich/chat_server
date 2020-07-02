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
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
@EnableScheduling   // 1.开启定时任务
public class AnalyseServices {
    Logger logger = LoggerFactory.getLogger(getClass());

    private static final long APP_DRIVER = 1;//司机端应用id
    private static final long APP_CLIENT = 2;//乘客端应用id
    private static final String CRON_REG = "0 0 10 * * ?";

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
                    if (appInfo.getAppId() == APP_DRIVER || appInfo.getAppId() == APP_CLIENT) {
                        logger.info("yesterdayStartDate:" + DateTimeUtils.getYesterdayStartDate() + ", yesterdayEndDate:" + DateTimeUtils.getYesterdayEndDate());
                        int count = riskInfoWrapper.countSecurityNum(appInfo.getAppId(), DateTimeUtils.getYesterdayStartDate(), DateTimeUtils.getYesterdayEndDate());
                        int loginUserCount = riskInfoWrapper.countSecurityUserNum(appInfo.getAppId(), DateTimeUtils.getYesterdayStartDate(), DateTimeUtils.getYesterdayEndDate());
                        if (count >= 0) {//不设阈值
                            logger.info("risk count:" + count);
                            mWechatController.sendSecurityTemplateMsg(appInfo.getAppName(), "设备风险",
                                    "在过去的" + DateTimeUtils.getYesterday() + "一天内总共有" + count
                                            + "条设备风险数据[" + mEnv + "]，其中共" + loginUserCount + "个登录用户。发送ip:"
                                            + IpUtil.getLocalIP(), "请至APP记录平台查看完整详细信息", null);
                        } else {
                            logger.info("appId[" + appInfo.getAppId() + "]periodCheckSecurity method run,find no risk");
                        }
                    }
                }
            }
        }
    }

}
