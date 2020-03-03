package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.bean.UserEntity;

import java.util.Date;

/**
 * 记录埋点的服务
 */
public interface IRecordService {

    void saveRecord(RecordInfo data, Date uploadTime);
}
