package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.bean.RiskInfo;
import com.ruqi.appserver.ruqi.bean.UserEntity;

import java.util.Date;
import java.util.List;

/**
 * 记录埋点的服务
 */
public interface IRecordService {

    void saveRecord(RecordInfo<RiskInfo> data, Date uploadTime,String requestIp);
    List<RecordInfo<RiskInfo>> queryList(int pageIndex, int limit, RecordInfo<RiskInfo> params);
    int queryTotalSize(RecordInfo<RiskInfo> params);
}
