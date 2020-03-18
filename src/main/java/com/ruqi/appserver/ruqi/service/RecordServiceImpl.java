package com.ruqi.appserver.ruqi.service;

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
import java.util.List;

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
    public void saveRecord(RecordInfo<RiskInfo> data, Date uploadTime, String requestIp) {
        logger.info("upload data:" + data.toString() + ";uploadTime:" + uploadTime.getTime() + ";curThread:" + Thread.currentThread().getName());
        if (data.getRecordType() == RiskEnum.RUNTIME_RISK.getId()
                && data.getAppInfo() != null
                && data.getContent() != null
                && !MyStringUtils.isEmpty(data.getAppInfo().getAppKey())) {//
//           int appId=appInfoWrapper.getAppIdByKey("BB392D26CF521EFD");
            AppInfo appInfo = appInfoWrapper.getAppInfoByKey(data.getAppInfo().getAppKey());
            if (appInfo != null && appInfo.getAppId() > 0) {
                RiskInfo riskInfo = data.getContent();
                riskInfo.setAppId(appInfo.getAppId());
                riskInfo.setRequestIp(requestIp);
                saveRiskUserInfo(data.getUserInfo());
                saveRiskInfo(data.getContent());
            } else {
                logger.info("this appKey[" + data.getAppInfo().getAppKey() + "] not exists,throw this msg");
            }
        }
    }

    @Override
    public List<RecordRiskInfo> queryListForLayUi(int pageIndex, int limit, RecordInfo<RiskInfo> params) {
        return riskInfoWrapper.queryListForLayUi(pageIndex * limit, limit, params);
    }

    @Override
    public List<RecordInfo<RiskInfo>> queryList(int pageIndex, int limit, RecordInfo<RiskInfo> riskParams) {
        return riskInfoWrapper.queryRiskList(pageIndex * limit, limit, riskParams);
    }

    @Override
    public int queryTotalSize(RecordInfo<RiskInfo> riskParams) {
        return riskInfoWrapper.queryTotalSize(riskParams, 9);
    }

    private void saveRiskUserInfo(UserEntity userInfo) {
        if (userInfo != null) {
            long userID = userInfo.getUserId();
            if (userID > 0) {
                UserEntity userEntity = userWrapper.getOne(userID);
                if (userEntity != null) {
                    userWrapper.update(userInfo);
                } else {
                    userWrapper.insert(userInfo);
                }
            }
        }
    }

    private void saveRiskInfo(RiskInfo content) {
        if (content != null) {
            riskInfoWrapper.insert(content);
        }
    }


}
