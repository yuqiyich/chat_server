package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.controller.WechatController;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.crypto.Data;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling   // 1.开启定时任务
@EnableAsync     //开启异步线程
public class AnalyseServices {

    private static final long INTERVAL=1000*60*60;//上报时间间隔
    private static final long MAX_THRESHOLD=10;//最大上报数

    @Autowired
    RiskInfoWrapper riskInfoWrapper;

    @Autowired
    AppInfoWrapper appInfoWrapper;

    @Autowired
    WechatController mWechatController;

    @Async("taskExecutor")
    @Scheduled(fixedDelay = INTERVAL)
    public void periodCheckSecurity() throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(getClass());
        List<AppInfo> appInfos=appInfoWrapper.getAllApps();
        if (!CollectionUtils.isEmpty(appInfos)){
            for (AppInfo appInfo:appInfos) {
                int count = riskInfoWrapper.countSecurityNum(appInfo.getAppId(),new Date(System.currentTimeMillis()-INTERVAL),new Date());
               if (count>=MAX_THRESHOLD){
                   logger.debug("risk count:"+count);
                   // TODO: 2020/3/12 暂时测试每一个上报都进行微信通知
                   mWechatController.sendSecurityTemplateMsg(appInfo.getAppName(), "设备风险",
                           "在过去的"+(INTERVAL/60/1000)+"分钟内"+count+"条风险设备记录", "请至APP记录平台查看完整详细信息", null);
               } else {
                   logger.info("appId["+appInfo.getAppId()+"]periodCheckSecurity method run,find no risk");
               }
            }
          }
        }
}
