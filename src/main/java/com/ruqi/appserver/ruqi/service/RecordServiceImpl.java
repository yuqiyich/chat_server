package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.response.EventDayDataH5Hybrid;
import com.ruqi.appserver.ruqi.dao.entity.DeviceRiskOverviewEntity;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.utils.EncryptUtils;
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

//    @Autowired
//    RecordInfoDAO recordInfoDAO;

    @Override
    @Async("taskExecutor")
    public <T extends BaseRecordInfo> void saveRecord(RecordInfo<T> data, String requestIp) {
        Date uploadTime = new Date();
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
                if (data.getContent().getCreateTime() <= 0) {
                    data.getContent().setCreateTime(System.currentTimeMillis());
                }
                Date createDate = new Date();
                createDate.setTime(data.getContent().getCreateTime());
                data.getContent().setRecordTime(uploadTime);
                if (data.getRecordType() == RecordTypeEnum.RUNTIME_RISK.getId()
                        && data.getContent() instanceof RiskInfo) {
                    RiskInfo riskInfo = (RiskInfo) data.getContent();
                    if (data.getUserInfo() != null) {//user 信息没有就不保存
                        data.getUserInfo().setAppId(appInfo.getAppId());
                        if (!MyStringUtils.isEmpty(data.getUserInfo().getUserPhone())) {
                            data.getUserInfo().setUserPhone(EncryptUtils.encode(data.getUserInfo().getUserPhone()));
                        }
                        saveRiskUserInfo(data.getUserInfo());
                    }
                    saveRiskInfo(riskInfo, createDate);

                    String id = "121";
                    data.setId(id);
//                    recordInfoDAO.save(data);
//                    recordInfoDAO.findById(id);
                }
            } else {
                logger.info("this appKey[" + data.getAppInfo().getAppKey() + "] not exists,throw this msg");
            }
        }
    }

    @Override
    @Async("taskExecutor")
    public <T extends BaseUploadRecordInfo> void saveDotRecord(UploadRecordInfo<T> data, String requestIp) {
        Date uploadTime = new Date();
        logger.info("upload data:" + data.toString() + ";uploadTime:" + uploadTime.getTime());
        if (data.getAppInfo() != null
                && data.getContent() != null
                && !MyStringUtils.isEmpty(data.getAppInfo().getAppKey())) {//
//           int appId=appInfoWrapper.getAppIdByKey("BB392D26CF521EFD");
            logger.info("appInfoSevice:" + appInfoSevice.getClass().getClassLoader());
            AppInfo appInfo = appInfoSevice.getAppInfoByKey(data.getAppInfo().getAppKey());
            logger.info("appInfo id:" + "" + (appInfo != null ? appInfo.getAppId() : 0));
            if (appInfo != null && appInfo.getAppId() > 0) {
                if (null != data.getContent() && data.getRecordType() == RecordTypeEnum.DOT_EVENT_RECORD.getId()
                        && data.getContent() instanceof UploadDotEventInfo) {
                    if (data.getContent().getCreateTime() <= 0) {
                        data.getContent().setCreateTime(System.currentTimeMillis());
                    }
                    Date createDate = new Date();
                    createDate.setTime(data.getContent().getCreateTime());
                    saveDotEventRecord(data, createDate, appInfo.getAppId(), requestIp);
                }
            } else {
                logger.info("this appKey[" + data.getAppInfo().getAppKey() + "] not exists,throw this msg");
            }
        }
    }

    private <T extends BaseUploadRecordInfo> void saveDotEventRecord(UploadRecordInfo<T> uploadRecordInfo, Date createDate, int appId, String requestIp) {
        if (uploadRecordInfo != null && uploadRecordInfo.getContent() instanceof UploadDotEventInfo) {
            dotEventInfoWrapper.insertDotEventRecord((UploadRecordInfo<BaseUploadRecordInfo>) uploadRecordInfo, createDate, appId, new Date(), requestIp);
        }
    }

    @Override
    public List<RecordRiskInfo> queryListForLayUi(int pageIndex, int limit, RecordInfo<RiskInfo> params) {
        return riskInfoWrapper.queryListForLayUi(pageIndex * limit, limit, params);
    }

    @Override
    public List<DeviceRiskOverviewEntity> queryOverviewList(int pageIndex, int limit, RecordInfo<RiskOverviewInfo> params) {
        if (null != params && null != params.getContent()) {
            switch (params.getContent().overviewType) {
                case RiskOverviewInfo.TYPE_RISK_TYPE:
                    return riskInfoWrapper.queryOverviewRiskType(pageIndex * limit, limit, params);
                case RiskOverviewInfo.TYPE_APP_VERSION:
                    return riskInfoWrapper.queryOverviewAppVersion(pageIndex * limit, limit, params);
                case RiskOverviewInfo.TYPE_DEVICE_MODEL:
                    return riskInfoWrapper.queryOverviewDeviceModel(pageIndex * limit, limit, params);
                case RiskOverviewInfo.TYPE_DEVICE_BRAND:
                    return riskInfoWrapper.queryOverviewDeviceBrand(pageIndex * limit, limit, params);
                case RiskOverviewInfo.TYPE_PHONE_NUM:
                    return riskInfoWrapper.queryOverviewPhoneNum(pageIndex * limit, limit, params);
                case RiskOverviewInfo.TYPE_DEVICE_ID:
                    return riskInfoWrapper.queryOverviewDeviceId(pageIndex * limit, limit, params);
                case RiskOverviewInfo.TYPE_ANDROID_VERSION:
                    return riskInfoWrapper.queryOverviewAndroidVersion(pageIndex * limit, limit, params);
            }
        }
        return null;
    }

    @Override
    public List<RecordDotEventInfo> queryCommonEventListForLayui(int pageIndex, int limit, RecordInfo<DotEventInfo> params) {
        return dotEventInfoWrapper.queryCommonEventListForLayui(pageIndex * limit, limit, params);
    }

//    @Override
//    public List<String> queryEventDetails(String key) {
//        return dotEventInfoWrapper.queryEventDetails(key);
//    }

    @Override
    public List<String> queryAppVersionNameForLayui() {
        return riskInfoWrapper.queryAppVersionNameForLayui("versionNameList");
    }

    @Override
    public List<String> queryRiskTypeForLayui() {
        return riskInfoWrapper.queryRiskTypeForLayui("riskTypeList");
    }

    @Override
    public long queryTotalSize(RecordInfo<RiskInfo> riskParams) {
        return riskInfoWrapper.queryTotalSize(riskParams, 9);
    }

    @Override
    public long queryTotalSizeCommonEvent(RecordInfo<DotEventInfo> recordInfo) {
        return dotEventInfoWrapper.queryTotalSizeCommonEvent(recordInfo);
    }

    @Override
    public long queryEventTotalUserSize(RecordInfo<DotEventInfo> recordInfo) {
        return dotEventInfoWrapper.queryEventTotalUserSize(recordInfo);
    }

    @Override
    public long queryEventTotalOrderSize(RecordInfo<DotEventInfo> recordInfo) {
        return dotEventInfoWrapper.queryEventTotalOrderSize(recordInfo);
    }

    @Override
    public List<EventDayDataH5Hybrid> queryWeekDataH5Hybrid() {
        return dotEventInfoWrapper.queryWeekDataH5Hybrid();
    }

    @Override
    public long queryOverviewTotalSize(RecordInfo<RiskOverviewInfo> recordInfo) {
        if (null != recordInfo && null != recordInfo.getContent()) {
            switch (recordInfo.getContent().overviewType) {
                case RiskOverviewInfo.TYPE_RISK_TYPE:
                    return riskInfoWrapper.queryOverviewRiskTypeTotalSize(recordInfo);
                case RiskOverviewInfo.TYPE_APP_VERSION:
                    return riskInfoWrapper.queryOverviewAppVersionTotalSize(recordInfo);
                case RiskOverviewInfo.TYPE_DEVICE_MODEL:
                    return riskInfoWrapper.queryOverviewDeviceModelTotalSize(recordInfo);
                case RiskOverviewInfo.TYPE_DEVICE_BRAND:
                    return riskInfoWrapper.queryOverviewDeviceBrandTotalSize(recordInfo);
                case RiskOverviewInfo.TYPE_PHONE_NUM:
                    return riskInfoWrapper.queryOverviewPhoneNumTotalSize(recordInfo);
                case RiskOverviewInfo.TYPE_DEVICE_ID:
                    return riskInfoWrapper.queryOverviewDeviceIdTotalSize(recordInfo);
                case RiskOverviewInfo.TYPE_ANDROID_VERSION:
                    return riskInfoWrapper.queryOverviewAndroidVersionTotalSize(recordInfo);
            }
        }
        return 0;
    }

    private void saveRiskUserInfo(UserEntity userInfo) {
        if (userInfo != null) {
            long userID = userInfo.getUserId();
            if (userID > 0) {
                UserEntity userEntity = userWrapper.getOne(userID, userInfo.getAppId());
                if (userEntity != null) {
                    userWrapper.update(userInfo);
                } else {
                    userWrapper.insert(userInfo);
                }
            }
        }
    }

    private void saveRiskInfo(RiskInfo content, Date createDate) {
        if (content != null) {
            riskInfoWrapper.insert(content, createDate);
        }
    }


}
