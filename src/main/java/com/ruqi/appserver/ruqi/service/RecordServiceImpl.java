package com.ruqi.appserver.ruqi.service;

import com.aliyuncs.utils.StringUtils;
import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RecordServiceImpl implements IRecordService {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    AppInfoWrapper appInfoWrapper;
    @Autowired
    UserMapper userWrapper;
    @Autowired
    RiskInfoWrapper riskInfoWrapper;

    @Override
    @Async("taskExecutor")
    public void saveRecord(RecordInfo<RiskInfo> data, Date uploadTime) {
        logger.info("upload data:" + data.toString() + ";uploadTime:" + uploadTime.getTime() + ";curThread:" + Thread.currentThread().getName());
        if (data.getRecordType() == RiskEnum.RUNTIME_RISK.getId()
                && data.getAppInfo() != null
                && data.getContent() != null
                && !MyStringUtils.isEmpty(data.getAppInfo().getAppKey())) {//
//           int appId=appInfoWrapper.getAppIdByKey("BB392D26CF521EFD");

            int appId = appInfoWrapper.getAppIdByKey(data.getAppInfo().getAppKey());
            logger.info("appId："+ appId);
            if (appId > 0) {
                RiskInfo riskInfo = data.getContent();
                riskInfo.setAppId(appId);
                saveRiskUserInfo(data.getUserInfo());
                saveRiskInfo(data.getContent());
            } else {
                logger.info("this appKey[" + data.getAppInfo().getAppKey() + "] not exists,throw this msg");
            }
        }
    }

    private void saveRiskUserInfo(UserEntity userInfo) {
        if (userInfo != null) {
            long userID = userInfo.getUserId();
            UserEntity userEntity = userWrapper.getOne(userID);
            if (userEntity != null) {
                userWrapper.update(userInfo);
            } else {
                userWrapper.insert(userInfo);
            }
        }
    }

    private void saveRiskInfo(RiskInfo content) {
        if (content != null) {
            riskInfoWrapper.insert(content);
        }
    }


}
