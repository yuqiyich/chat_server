package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventDayDataH5Hybrid;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventDayItemDataGaiaRecmd;
import com.ruqi.appserver.ruqi.bean.dbbean.DBEventDayItemDataH5Hybrid;
import com.ruqi.appserver.ruqi.bean.response.EventDataGaiaRecmd;
import com.ruqi.appserver.ruqi.bean.response.EventDayDataH5Hybrid;
import com.ruqi.appserver.ruqi.constans.DotEventKey;
import com.ruqi.appserver.ruqi.dao.entity.DeviceRiskOverviewEntity;
import com.ruqi.appserver.ruqi.dao.mappers.DotEventInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.RiskInfoWrapper;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import com.ruqi.appserver.ruqi.utils.EncryptUtils;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
                    if (null == data.getUserInfo() || data.getUserInfo().getUserId() <= 0) {
                        UploadUserEntity uploadUserEntity = new UploadUserEntity();
                        uploadUserEntity.setUserId(((UploadDotEventInfo) data.getContent()).userId);
                        data.setUserInfo(uploadUserEntity);
                    }

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
        // 初始化要返回的结构体、日期
        List<EventDayDataH5Hybrid> resultList = new ArrayList<>();
        List<String> daysBetwwen = DateTimeUtils.getDaysBetwwen(6);
        for (String dateStr : daysBetwwen) {
            EventDayDataH5Hybrid item = new EventDayDataH5Hybrid();
            item.date = dateStr;
            resultList.add(item);
        }
        // sql查到相应数据，查询每天失败次数最多的用户，处理成接口需要返回的数据
//        int userDataCount = 0;
        List<DBEventDayDataH5Hybrid> dbEventDayDataH5HybridList = dotEventInfoWrapper.queryWeekDataUserCountH5Hybrid();
        for (DBEventDayDataH5Hybrid item : dbEventDayDataH5HybridList) {
            // 每一条数据，需要变更返回结果中的某一条
            int dataindex = daysBetwwen.indexOf(item.date);
            if (-1 != dataindex) {
                EventDayDataH5Hybrid eventDayUserH5Hybrid = resultList.get(dataindex);
                // sql已经倒序排列，所以每一个日期取第一个数据的失败最多数据
                if (eventDayUserH5Hybrid.moreFailCount == 0) {
                    eventDayUserH5Hybrid.moreFailCount = item.failCount;
                    eventDayUserH5Hybrid.moreFailUserId = item.userId;
                    eventDayUserH5Hybrid.moreFailPlatform = item.platform;
//                    userDataCount++;
//                    if (userDataCount == resultList.size()) {
//                        break;
//                    }
                }
                // 区分平台，用户数+1
                eventDayUserH5Hybrid.failUserCountTotal++;
                if (BaseRecordInfo.PLATFORM_IOS.equals(item.platform)) {
                    eventDayUserH5Hybrid.failUserCountIOS++;
                } else if (BaseRecordInfo.PLATFORM_ANDROID.equals(item.platform)) {
                    eventDayUserH5Hybrid.failUserCountAndroid++;
                }
            }
        }
        // sql查看每天每个平台每个key的数量，处理
        List<DBEventDayItemDataH5Hybrid> dbEventDayItemDataH5Hybrids = dotEventInfoWrapper.queryWeekDataH5Hybrid();
        for (DBEventDayItemDataH5Hybrid item : dbEventDayItemDataH5Hybrids) {
            // 每一条数据，需要变更返回结果中的某一条
            int dataindex = daysBetwwen.indexOf(item.date);
            if (-1 != dataindex) {
                EventDayDataH5Hybrid eventDayUserH5Hybrid = resultList.get(dataindex);
                if (BaseRecordInfo.PLATFORM_IOS.equals(item.platform)) {
                    switch (item.eventKey) {
                        case DotEventKey.H5Load.H5_HYBRID_LOAD_SUCCESS:
                            eventDayUserH5Hybrid.successCountI = item.totalCount;
                            break;
                        case DotEventKey.H5Load.H5_HYBRID_LOAD_FAIL:
                            eventDayUserH5Hybrid.failCountI = item.totalCount;
                            break;
                        case DotEventKey.H5Load.H5_HYBRID_RELOAD_SUCCESS:
                            eventDayUserH5Hybrid.reloadSuccessCountI = item.totalCount;
                            break;
                        case DotEventKey.H5Load.H5_HYBRID_RELOAD_FAIL:
                            eventDayUserH5Hybrid.reloadFailCountI = item.totalCount;
                            break;
                    }
                } else if (BaseRecordInfo.PLATFORM_ANDROID.equals(item.platform)) {
                    switch (item.eventKey) {
                        case DotEventKey.H5Load.H5_HYBRID_LOAD_SUCCESS:
                            eventDayUserH5Hybrid.successCountA = item.totalCount;
                            break;
                        case DotEventKey.H5Load.H5_HYBRID_LOAD_FAIL:
                            eventDayUserH5Hybrid.failCountA = item.totalCount;
                            break;
                        case DotEventKey.H5Load.H5_HYBRID_RELOAD_SUCCESS:
                            eventDayUserH5Hybrid.reloadSuccessCountA = item.totalCount;
                            break;
                        case DotEventKey.H5Load.H5_HYBRID_RELOAD_FAIL:
                            eventDayUserH5Hybrid.reloadFailCountA = item.totalCount;
                            break;
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public List<EventDataGaiaRecmd> queryWeekDataGaiaRecmd(String appId) {
        // 初始化要返回的结构体、日期
        List<EventDataGaiaRecmd> resultList = new ArrayList<>();
        List<String> daysBetwwen = DateTimeUtils.getDaysBetwwen(6);
        for (String dateStr : daysBetwwen) {
            EventDataGaiaRecmd item = new EventDataGaiaRecmd();
            item.date = dateStr;
            resultList.add(item);
        }

        // sql查看每天每个平台每个key的数量，处理
        List<DBEventDayItemDataGaiaRecmd> allGaiaRecmds = dotEventInfoWrapper.queryWeekDataGaiaRecmd(appId, false);
        for (DBEventDayItemDataGaiaRecmd item : allGaiaRecmds) {
            // 每一条数据，需要变更返回结果中的某一条
            int dataindex = daysBetwwen.indexOf(item.date);
            if (-1 != dataindex) {
                EventDataGaiaRecmd eventDataGaiaRecmd = resultList.get(dataindex);
                if (BaseRecordInfo.PLATFORM_IOS.equals(item.platform)) {
                    eventDataGaiaRecmd.gaiaRecmdCountI = item.totalCount;
                } else if (BaseRecordInfo.PLATFORM_ANDROID.equals(item.platform)) {
                    eventDataGaiaRecmd.gaiaRecmdCountA = item.totalCount;
                }
            }
        }
        List<DBEventDayItemDataGaiaRecmd> orderGaiaRecmds = dotEventInfoWrapper.queryWeekDataGaiaRecmd(appId, true);
        for (DBEventDayItemDataGaiaRecmd item : orderGaiaRecmds) {
            // 每一条数据，需要变更返回结果中的某一条
            int dataindex = daysBetwwen.indexOf(item.date);
            if (-1 != dataindex) {
                EventDataGaiaRecmd eventDataGaiaRecmd = resultList.get(dataindex);
                if (BaseRecordInfo.PLATFORM_IOS.equals(item.platform)) {
                    eventDataGaiaRecmd.gaiaRecmdOrderCountI = item.totalCount;
                } else if (BaseRecordInfo.PLATFORM_ANDROID.equals(item.platform)) {
                    eventDataGaiaRecmd.gaiaRecmdOrderCountA = item.totalCount;
                }
            }
        }
        return resultList;
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
            if (!MyStringUtils.isEmpty(content.getRiskDetail()) && content.getRiskDetail().length() > 250) {
                content.setRiskDetail(content.getRiskDetail().substring(0, 250));
            }
            riskInfoWrapper.insert(content, createDate);
        }
    }


}
