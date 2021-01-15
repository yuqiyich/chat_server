package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.dao.mappers.SentryConfigWrapper;
import com.ruqi.appserver.ruqi.request.*;
import jodd.util.StringUtil;
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
        SentryConfigEntity sentryConfigEntity = new SentryConfigEntity();
        sentryConfigEntity.setSentrySwitch("0");
        sentryConfigEntity.setLevel("d");
        sentryConfigEntity.setDns(DNS);
        List<SentryConfigEntity> sentryConfigEntities = sentryConfigWrapper.getSentryConfig();
        if(sentryConfigEntities != null && sentryConfigEntities.size() > 0){
            SentryConfigEntity sentryConfigEntity1 = sentryConfigEntities.get(0);
            if(!StringUtil.isEmpty(sentryConfigEntity1.getSentrySwitch()) && sentryConfigEntity1.getSentrySwitch().equals("1")){//系统开关打开
                List<SentryPlatformEntity> sentryPlatformEntities = sentryConfigWrapper.getSentryPlatform(sentryConfigRequest.getPlatform(), sentryConfigRequest.getEnvironment());
                if(sentryPlatformEntities != null && sentryPlatformEntities.size() > 0){
                    SentryPlatformEntity sentryPlatformEntity = sentryPlatformEntities.get(0);
                    boolean envEnable = !StringUtil.isEmpty(sentryConfigRequest.getEnvironment())
                            && !StringUtil.isEmpty(sentryPlatformEntity.getEnvironment())
                            && sentryConfigRequest.getEnvironment().equals(sentryPlatformEntity.getEnvironment());
                    boolean platformEnable = !StringUtil.isEmpty(sentryConfigRequest.getPlatform())
                            && !StringUtil.isEmpty(sentryPlatformEntity.getPlatform())
                            && sentryConfigRequest.getPlatform().equals(sentryPlatformEntity.getPlatform());
                    if(envEnable && platformEnable) {
                        List<SentryAreaEntity> sentryAreaEntities = sentryConfigWrapper.getSentryArea(sentryConfigRequest.getAreaCode(), sentryConfigRequest.getCityCode());
                        if(sentryAreaEntities != null && sentryAreaEntities.size() > 0){
                            SentryAreaEntity sentryAreaEntity = sentryAreaEntities.get(0);
                            boolean areaCodeEnable = !StringUtil.isEmpty(sentryAreaEntity.getAreacode())
                                    && !StringUtil.isEmpty(sentryConfigRequest.getAreaCode())
                                    && sentryConfigRequest.getAreaCode().equals(sentryAreaEntity.getAreacode());
                            boolean cityCodeEnable = !StringUtil.isEmpty(sentryAreaEntity.getCitycode())
                                    && !StringUtil.isEmpty(sentryConfigRequest.getCityCode())
                                    && sentryConfigRequest.getCityCode().equals(sentryAreaEntity.getCitycode());
                            if(cityCodeEnable || areaCodeEnable){
                                return sentryConfigEntity1;
                            }else{
                                List<SentryMonitoringObjectEntity> sentryMonitoringObjectEntityList = sentryConfigWrapper.getSentryMonitoringObject(sentryConfigRequest.getMobile());
                                if(sentryMonitoringObjectEntityList != null && sentryMonitoringObjectEntityList.size() > 0){//白名单
                                    return sentryConfigEntity1;
                                }
                                return sentryConfigEntity;
                            }
                        }else{
                            List<SentryMonitoringObjectEntity> sentryMonitoringObjectEntityList = sentryConfigWrapper.getSentryMonitoringObject(sentryConfigRequest.getMobile());
                            if(sentryMonitoringObjectEntityList != null && sentryMonitoringObjectEntityList.size() > 0){//白名单
                                return sentryConfigEntity1;
                            }
                            return sentryConfigEntity;
                        }
                    }else{
                        List<SentryMonitoringObjectEntity> sentryMonitoringObjectEntityList = sentryConfigWrapper.getSentryMonitoringObject(sentryConfigRequest.getMobile());
                        if(sentryMonitoringObjectEntityList != null && sentryMonitoringObjectEntityList.size() > 0){//白名单
                            return sentryConfigEntity1;
                        }
                        return sentryConfigEntity;
                    }
                }else{
                    List<SentryMonitoringObjectEntity> sentryMonitoringObjectEntityList = sentryConfigWrapper.getSentryMonitoringObject(sentryConfigRequest.getMobile());
                    if(sentryMonitoringObjectEntityList != null && sentryMonitoringObjectEntityList.size() > 0){//白名单
                        return sentryConfigEntity1;
                    }
                    return sentryConfigEntity;
                }
            }else{//系统开关关闭
                return sentryConfigEntity;
            }
        }
        return sentryConfigEntity;
    }
}
