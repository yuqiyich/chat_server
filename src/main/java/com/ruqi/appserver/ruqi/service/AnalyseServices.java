package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppResponeInfo;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventDayDataRecPoint;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.kafka.BaseKafkaLogInfo;
import com.ruqi.appserver.ruqi.kafka.KafkaProducer;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import com.ruqi.appserver.ruqi.utils.EnvUtils;
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

    private static final String CRON_REG = "0 0 10 * * ?";
    private static final String CRON_STATIC = "0 0 3 * * ?";

    @Autowired
    RiskInfoWrapper riskInfoWrapper;

    @Autowired
    AppInfoWrapper appInfoWrapper;

    @Autowired
    DotEventInfoWrapper dotEventInfoWrapper;

    @Autowired
    private IPointRecommendService iPointRecommendService;

//    @Autowired
//    WechatController mWechatController;

    @Autowired
    protected KafkaProducer kafkaProducer;

    @Value("${spring.profiles.active}")
    private String mEnv = "";

    @Scheduled(cron = CRON_REG)
    public void periodCheckSecurity() throws InterruptedException {
        if ("prod".equals(mEnv)) {
            List<AppResponeInfo> appInfos = appInfoWrapper.getAllApps();
            if (!CollectionUtils.isEmpty(appInfos)) {
                for (AppResponeInfo appInfo : appInfos) {
                    if (appInfo.appId == EnvUtils.APP_DRIVER || appInfo.appId == EnvUtils.APP_CLIENT) {
                        logger.info("yesterdayStartDate:" + DateTimeUtils.getYesterdayStartDate() + ", yesterdayEndDate:" + DateTimeUtils.getYesterdayEndDate());
                        int count = riskInfoWrapper.countSecurityNum(appInfo.appId, DateTimeUtils.getYesterdayStartDate(), DateTimeUtils.getYesterdayEndDate());
                        int loginUserCount = riskInfoWrapper.countSecurityUserNum(appInfo.appId, DateTimeUtils.getYesterdayStartDate(), DateTimeUtils.getYesterdayEndDate());
                        if (count >= 0) {//不设阈值
                            logger.info("risk count:" + count);
//                            mWechatController.sendSecurityTemplateMsg(appInfo.appName, "设备风险",
//                                    "在过去的" + DateTimeUtils.getYesterday() + "一天内总共有" + count
//                                            + "条设备风险数据[" + mEnv + "]，其中共" + loginUserCount + "个登录用户。发送ip:"
//                                            + IpUtil.getLocalIP(), "请至APP记录平台查看完整详细信息", null);
                            // 调用异步方法
                            kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.WARN,
                                    String.format("appName:[%s], content:[%s]",
                                            appInfo.appName,
                                            "在过去的" + DateTimeUtils.getYesterday() + "一天内总共有" + count
                                                    + "条设备风险数据[" + mEnv + "]，其中共" + loginUserCount + "个登录用户。发送ip:"
                                                    + IpUtil.getLocalIP()));
                        } else {
                            logger.info("appId[" + appInfo.appId + "]periodCheckSecurity method run,find no risk");
                        }
                    }
                }
            }
        }
    }

    @Scheduled(cron = CRON_STATIC)
    public void periodStaticRecommendData() throws InterruptedException {
//        analyseRecommendData(DEV);
//        analyseRecommendData(PRO);
        logger.info("定时更新推荐上车点数据");
        iPointRecommendService.staticRecommendPointByAdCode();

        // 统计前一天的乘客推荐点事件数据
        analyseRecPointYesterdayData();
    }

    // 测试
//    @Scheduled(cron = "0 33 * * * ?")
//    public void test() {
//        iPointRecommendService.staticRecommendPointByAdCode();

//        int appId = EnvUtils.APP_CLIENT;
//        for (int i = 192; i >= 1 ;i--) {
//            DBEventDayDataRecPoint dbEventDayDataRecPoint = dotEventInfoWrapper.queryRecPointYesterdayData(appId,
//                    DateTimeUtils.getGapDayStartDate(-i), DateTimeUtils.getGapDayEndDate(-i));
//            dbEventDayDataRecPoint.date = DateTimeUtils.getGapDayStartDate(-i);
//            dbEventDayDataRecPoint.env = EnvUtils.getAppEnvStr(appId);
//            dotEventInfoWrapper.saveRecPointDayData(dbEventDayDataRecPoint);
//        }
//    }

    private void analyseRecPointYesterdayData() {
        queryAndSaveRecPointYesterdayData(EnvUtils.APP_CLIENT);
        queryAndSaveRecPointYesterdayData(EnvUtils.APP_CLIENT_DEV);
    }

    private void queryAndSaveRecPointYesterdayData(int appId) {
        DBEventDayDataRecPoint dbEventDayDataRecPoint = dotEventInfoWrapper.queryRecPointYesterdayData(appId,
                DateTimeUtils.getYesterdayStartDate(), DateTimeUtils.getYesterdayEndDate());
        dbEventDayDataRecPoint.date = DateTimeUtils.getYesterdayStartDate();
        dbEventDayDataRecPoint.env = EnvUtils.getAppEnvStr(appId);
        dotEventInfoWrapper.saveRecPointDayData(dbEventDayDataRecPoint);
    }

    public void analyseRecommendData(String env) {
        //总的上报次数
        int uploadTimes = RPHandleManager.getIns().getTotalUploadTimes(env);
        //昨日天上报的原始记录次数
        int lastUplaodTimes = RPHandleManager.getIns().getLastDayUploadTimes(env);
        //一天新增的扎针点和推荐关系表
        int lastdayRecommendDataCount = RPHandleManager.getIns().getLastDayRecommendDataCount(env);
        //新增的扎针点和推荐关系表
        int totalRecommendDataCounts = RPHandleManager.getIns().getTotalRecommendDataCount(env);
        //一天新增的推荐点数目
        int lastDayRecommendPointCount = RPHandleManager.getIns().getLastDayRecommendPointCount(env);
        //推荐点总数
        int totalRecommendPointCounts = RPHandleManager.getIns().getTotalRecommendPointCount(env);
        StringBuilder res = new StringBuilder();
        res.append(env + "总的上报次数          :" + uploadTimes + "===")
                .append(DateTimeUtils.getYesterday() + "一天上报的原始记录次数：" + lastUplaodTimes + "\n")
                .append(env + "扎针点和推荐关系表总数  :" + totalRecommendDataCounts + "===")
                .append(DateTimeUtils.getYesterday() + "一天扎针点和推荐关系表次数：" + lastdayRecommendDataCount + "\n")
                .append(env + "推荐点总数            :" + totalRecommendPointCounts + "===")
                .append(DateTimeUtils.getYesterday() + "一天新增的推荐点：" + lastDayRecommendPointCount + "\n")
        ;
        logger.info("盖亚数据统计：" + res.toString());
        kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.WARN, res.toString());
    }

}
