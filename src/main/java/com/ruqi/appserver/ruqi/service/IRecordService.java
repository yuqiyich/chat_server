package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;

import java.util.Date;
import java.util.List;

/**
 * 记录埋点的服务
 */
public interface IRecordService {

    // 设备安全、打点事件（推荐点降级生效）
    <T extends BaseRecordInfo> void saveRecord(RecordInfo<T> data, Date uploadTime, String requestIp);

    List<RecordRiskInfo> queryListForLayUi(int pageIndex, int limit, RecordInfo<RiskInfo> params);

    List<RecordInfo<RiskInfo>> queryList(int pageIndex, int limit, RecordInfo<RiskInfo> params);

    long queryTotalSize(RecordInfo<RiskInfo> params);

    List<String> queryAppVersionNameForLayui();

    List<String> queryRiskTypeForLayui();

    List<RecordDotEventInfo> queryEventRecmdPointListForLayui(int i, int limit, RecordInfo<DotEventInfo> params);

    long queryTotalSizeEventRecmdPoint(RecordInfo<DotEventInfo> params);
}
