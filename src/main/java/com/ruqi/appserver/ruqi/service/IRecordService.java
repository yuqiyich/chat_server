package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;
import com.ruqi.appserver.ruqi.dao.entity.DeviceRiskOverviewEntity;

import java.util.Date;
import java.util.List;

/**
 * 记录埋点的服务
 */
public interface IRecordService {

    // 设备安全、打点事件（推荐点降级生效）
    <T extends BaseRecordInfo> void saveRecord(RecordInfo<T> data, Date uploadTime, String requestIp);

    List<RecordRiskInfo> queryListForLayUi(int pageIndex, int limit, RecordInfo<RiskInfo> params);

//    List<RecordInfo<RiskInfo>> queryList(int pageIndex, int limit, RecordInfo<RiskInfo> params);

    long queryTotalSize(RecordInfo<RiskInfo> params);

    List<String> queryAppVersionNameForLayui();

    List<String> queryRiskTypeForLayui();

    List<DeviceRiskOverviewEntity> queryOverviewList(int i, int limit, RecordInfo<RiskOverviewInfo> params);

    long queryOverviewTotalSize(RecordInfo<RiskOverviewInfo> params);

    List<RecordDotEventInfo> queryCommonEventListForLayui(int i, int limit, RecordInfo<DotEventInfo> params);

//    List<String> queryEventDetails(String key);

    long queryTotalSizeCommonEvent(RecordInfo<DotEventInfo> params);

    long queryEventTotalUserSize(RecordInfo<DotEventInfo> params);

    long queryEventTotalOrderSize(RecordInfo<DotEventInfo> params);
}
