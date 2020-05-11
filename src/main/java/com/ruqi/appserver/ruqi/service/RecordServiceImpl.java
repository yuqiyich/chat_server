package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
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
    AppInfoSevice appInfoSevice;
    @Autowired
    UserMapper userWrapper;
    @Autowired
    RiskInfoWrapper riskInfoWrapper;
    @Autowired
    DotEventInfoWrapper dotEventInfoWrapper;

    @Override
    @Async("taskExecutor")
    public <T extends BaseRecordInfo> void saveRecord(RecordInfo<T> data, Date uploadTime, String requestIp) {
        logger.info("upload data:" + data.toString() + ";uploadTime:" + uploadTime.getTime());
        if (data.getAppInfo() != null
                && data.getContent() != null
                && !MyStringUtils.isEmpty(data.getAppInfo().getAppKey())) {//
//           int appId=appInfoWrapper.getAppIdByKey("BB392D26CF521EFD");
            logger.info("appInfoSevice:" + appInfoSevice.getClass().getClassLoader());
            AppInfo appInfo = appInfoSevice.getAppInfoByKey(data.getAppInfo().getAppKey());
            logger.info("appInfo id:" + "" + (appInfo != null ? appInfo.getAppId() : 0));
            if (appInfo != null && appInfo.getAppId() > 0) {
                data.getContent().setAppId(appInfo.getAppId());
                data.getContent().setRequestIp(requestIp);
                // 记录时间使用服务器的时间
                data.getContent().setCreateTime(uploadTime);
                if (data.getRecordType() == RecordTypeEnum.RUNTIME_RISK.getId()
                        && data.getContent() instanceof RiskInfo) {
                    RiskInfo riskInfo = (RiskInfo) data.getContent();
                    if (data.getUserInfo() != null) {//user 信息没有就不保存
                        data.getUserInfo().setAppId(appInfo.getAppId());
                        saveRiskUserInfo(data.getUserInfo());
                    }
                    saveRiskInfo(riskInfo);
                } else if (data.getRecordType() == RecordTypeEnum.RECOMMEND_POINT_RISK.getId() && data.getContent() instanceof DotEventInfo) {
                    saveDotEventRecord((DotEventInfo) data.getContent());
                }
            } else {
                logger.info("this appKey[" + data.getAppInfo().getAppKey() + "] not exists,throw this msg");
            }
        }
    }

    private void saveDotEventRecord(DotEventInfo dotEventInfo) {
        if (dotEventInfo != null) {
            dotEventInfoWrapper.insertDotEventRecord(dotEventInfo);
        }
    }

    @Override
    public List<RecordRiskInfo> queryListForLayUi(int pageIndex, int limit, RecordInfo<RiskInfo> params) {
        return riskInfoWrapper.queryListForLayUi(pageIndex * limit, limit, params);
    }

    @Override
    public List<RecordDotEventInfo> queryEventRecmdPointListForLayui(int pageIndex, int limit, RecordInfo<DotEventInfo> params) {
        return dotEventInfoWrapper.queryEventRecmdPointListForLayui(pageIndex * limit, limit, params);
    }

    @Override
    public List<String> queryAppVersionNameForLayui() {
        return riskInfoWrapper.queryAppVersionNameForLayui("versionNameList");
    }

    @Override
    public List<String> queryRiskTypeForLayui() {
        return riskInfoWrapper.queryRiskTypeForLayui("riskTypeList");
    }

    @Override
    public List<RecordInfo<RiskInfo>> queryList(int pageIndex, int limit, RecordInfo<RiskInfo> riskParams) {
        return riskInfoWrapper.queryRiskList(pageIndex * limit, limit, riskParams);
    }

    @Override
    public long queryTotalSize(RecordInfo<RiskInfo> riskParams) {
        return riskInfoWrapper.queryTotalSize(riskParams, 9);
    }

    @Override
    public long queryTotalSizeEventRecmdPoint(RecordInfo<DotEventInfo> recordInfo) {
        return dotEventInfoWrapper.queryTotalSizeEventRecmdPoint(recordInfo);
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
