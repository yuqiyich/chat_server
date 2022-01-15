package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.dao.mappers.SentryConfigWrapper;
import com.ruqi.appserver.ruqi.request.*;
import jodd.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentryConfigServiceImpl implements ISentryConfigService {
    Logger logger = LoggerFactory.getLogger(getClass());
    private static final String DNS = "https://a938191ab6fc49cbb2216b1e7a672e88@poseidon-report.ruqimobility.com/2";

    @Autowired
    SentryConfigWrapper sentryConfigWrapper;

    @Override
    public SentryConfigEntity querySentryConfig(SentryConfigRequest sentryConfigRequest) {
        if (StringUtils.isEmpty(sentryConfigRequest.getProject())) {
            return handlerV1SentryConfig(sentryConfigRequest);
        } else {
            return handleV2SentryConfig(sentryConfigRequest);
        }

    }

    private SentryConfigEntity handleV2SentryConfig(SentryConfigRequest sentryConfigRequest) {
        SentryConfigEntity defaultSentryConfigEntity = new SentryConfigEntity(); //默认的配置是不开启sentry的
        defaultSentryConfigEntity.setSentrySwitch("0");
        defaultSentryConfigEntity.setLevel("d");
        defaultSentryConfigEntity.setDns(DNS);
        List<SentryConfigEntity> sentryConfigEntities = sentryConfigWrapper.getSentryConfigByProject(sentryConfigRequest.getProject(),  sentryConfigRequest.getPlatform());
        if (sentryConfigEntities != null && sentryConfigEntities.size() > 0) {//WARN应该是只有一个配置项
            SentryConfigEntity sentryConfigEntity1 = sentryConfigEntities.get(0);
            List<String> tags = sentryConfigWrapper.getSentryTagsByProject(sentryConfigEntity1.getId());
            sentryConfigEntity1.setTags(tags);
            int count = sentryConfigWrapper.getSentryHttpConfigByProject(sentryConfigEntity1.getId());
            logger.info("****max api count:"+count);
            sentryConfigEntity1.setLoopMaxCount(count);
            if (!StringUtil.isEmpty(sentryConfigEntity1.getSentrySwitch()) && sentryConfigEntity1.getSentrySwitch().equals("1")) {//系统开关打开
                List<SentryMonitoringObjectEntity> sentryMonitoringObjectEntityList = sentryConfigWrapper.getSentryMonitoringObjectByProject(sentryConfigRequest.getMobile(), sentryConfigEntity1.getId());
                if (sentryMonitoringObjectEntityList != null && sentryMonitoringObjectEntityList.size() > 0) {//命中用户白名单
                    sentryConfigEntity1.setLevel("i");//命中白名单的用户日志级别默认最高
                    return sentryConfigEntity1;
                }
                if (sentryConfigEntity1.getAreaSwitch()==1){//默认是开启
                    List<SentryAreaEntity> sentryAreaEntities = sentryConfigWrapper.getSentryAreaByProject(sentryConfigRequest.getAreaCode(), sentryConfigRequest.getCityCode(), sentryConfigEntity1.getId());
                    if (sentryAreaEntities != null && sentryAreaEntities.size() > 0) {
                        return sentryConfigEntity1;
                    } else {
                        return defaultSentryConfigEntity;
                    }
                } else {
                    return sentryConfigEntity1;
                }
            }
        }
        return defaultSentryConfigEntity;
    }

    /**
     * 之前老版本的获取配置文件
     *
     * @param sentryConfigRequest
     * @return
     */
    private SentryConfigEntity handlerV1SentryConfig(SentryConfigRequest sentryConfigRequest) {
        SentryConfigEntity defaultSentryConfigEntity = new SentryConfigEntity(); //默认的配置是不开启sentry的
        defaultSentryConfigEntity.setSentrySwitch("0");
        defaultSentryConfigEntity.setLevel("d");
        defaultSentryConfigEntity.setDns(DNS);
        List<SentryConfigEntity> sentryConfigEntities = sentryConfigWrapper.getSentryConfig();
        List<String> tags = sentryConfigWrapper.getSentryTags(sentryConfigRequest.getPlatform());
        if (sentryConfigEntities != null && sentryConfigEntities.size() > 0) {//WARN 目前只是取了第一项配置项
            SentryConfigEntity sentryConfigEntity1 = sentryConfigEntities.get(0);
            sentryConfigEntity1.setTags(tags);
            if (!StringUtil.isEmpty(sentryConfigEntity1.getSentrySwitch()) && sentryConfigEntity1.getSentrySwitch().equals("1")) {//系统开关打开
                List<SentryPlatformEntity> sentryPlatformEntities = sentryConfigWrapper.getSentryPlatform(sentryConfigRequest.getPlatform(), sentryConfigRequest.getEnvironment());
                if (sentryPlatformEntities != null && sentryPlatformEntities.size() > 0) {//获取平台和环境
                    List<SentryMonitoringObjectEntity> sentryMonitoringObjectEntityList = sentryConfigWrapper.getSentryMonitoringObject(sentryConfigRequest.getMobile());
                    if (sentryMonitoringObjectEntityList != null && sentryMonitoringObjectEntityList.size() > 0) {//命中用户白名单
                        sentryConfigEntity1.setLevel("i");//命中白名单的用户日志级别默认最高
                        return sentryConfigEntity1;
                    }
                    List<SentryAreaEntity> sentryAreaEntities = sentryConfigWrapper.getSentryArea(sentryConfigRequest.getAreaCode(), sentryConfigRequest.getCityCode());
                    if (sentryAreaEntities != null && sentryAreaEntities.size() > 0) {
                        return sentryConfigEntity1;
                    } else {
                        return defaultSentryConfigEntity;
                    }
                }
            }
        }
        return defaultSentryConfigEntity;
    }
}
