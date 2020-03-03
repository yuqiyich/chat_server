package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.AppInfo;
import com.ruqi.appserver.ruqi.bean.RecordInfo;
import com.ruqi.appserver.ruqi.bean.RiskEnum;
import com.ruqi.appserver.ruqi.bean.UserEntity;
import com.ruqi.appserver.ruqi.dao.mappers.AppInfoWrapper;
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

    @Override
    @Async("taskExecutor")
    public void saveRecord(RecordInfo data, Date uploadTime) {
        logger.info("upload data:" + data.toString() + ";uploadTime:" + uploadTime.getTime() + ";curThread:" + Thread.currentThread().getName() + "id：" + appInfoWrapper.getAppIdByKey("BB392D26CF521EFD"));
        if (data.getRecordType() == RiskEnum.RUNTIME_RISK.getId()) {

        }
    }

    /**
     * @param data
     * @param userEntity
     * @param appInfo
     * @param uploadTime
     */
    private void saveRecord(String data, UserEntity userEntity, AppInfo appInfo, Date uploadTime) {

    }

    ;
}
