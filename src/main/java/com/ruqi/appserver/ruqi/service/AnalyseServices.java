package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.controller.WechatController;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
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
@EnableAsync     //开启异步线程
public class AnalyseServices {

    private static final long INTERVAL = 1000 * 60 * 60;//上报时间间隔
    private static final long MAX_THRESHOLD = 100; // 上报数阀值

    @Autowired
    RiskInfoWrapper riskInfoWrapper;

    @Autowired
    AppInfoWrapper appInfoWrapper;

    @Autowired
    WechatController mWechatController;

    @Value("${spring.profiles.active}")
    private String mEnv = "";

    @Async("taskExecutor")
    @Scheduled(fixedDelay = INTERVAL)
    public void periodCheckSecurity() throws InterruptedException {
        if ("prod".equals(mEnv)) {
            Logger logger = LoggerFactory.getLogger(getClass());
            List<AppInfo> appInfos = appInfoWrapper.getAllApps();
            if (!CollectionUtils.isEmpty(appInfos)) {
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.getAppId() == 1 || appInfo.getAppId() == 2) {
                        int count = riskInfoWrapper.countSecurityNum(appInfo.getAppId(), new Date(System.currentTimeMillis() - INTERVAL), new Date());
                        if (count >= MAX_THRESHOLD) {
                            logger.debug("risk count:" + count);
                            mWechatController.sendSecurityTemplateMsg(appInfo.getAppName(), "设备风险",
                                    "在过去的" + (INTERVAL / 60 / 1000) + "分钟内" + count + "条设备风险数据[" + mEnv + "],发送ip:" + IpUtil.getLocalIP(), "请至APP记录平台查看完整详细信息", null);
                        } else {
                            logger.info("appId[" + appInfo.getAppId() + "]periodCheckSecurity method run,find no risk");
                        }
                    }
                }
            }
        }
    }
}
