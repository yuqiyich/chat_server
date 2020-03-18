package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.*;

import java.util.Date;
import java.util.List;

/**
 * 记录埋点的服务
 */
public interface IRecordService {

    void saveRecord(RecordInfo<RiskInfo> data, Date uploadTime,String requestIp);
    List<RecordRiskInfo> queryListForLayUi(int pageIndex, int limit, RecordInfo<RiskInfo> params);
    List<RecordInfo<RiskInfo>> queryList(int pageIndex, int limit, RecordInfo<RiskInfo> params);
    int queryTotalSize(RecordInfo<RiskInfo> params);
}
