package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.controller.PointController;
import com.ruqi.appserver.ruqi.kafka.BaseKafkaLogInfo;
import com.ruqi.appserver.ruqi.kafka.KafkaProducer;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.utils.EnvUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruqi.appserver.ruqi.geomesa.RPHandleManager.DEV;
import static com.ruqi.appserver.ruqi.geomesa.RPHandleManager.PRO;


/**
 * 用来一点点的处理redis里面储存的数据
 */
@Configuration
@EnableScheduling   // 1.开启定时任务
public class PointSaveConsumer  {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    protected KafkaProducer kafkaProducer;

    @Autowired
    protected IPointRecommendService pointRecommendService;

    @Autowired
    RedisUtil redisUtil;


    public static int POINT_SAVE_BUCKET_SIZE = 80;
    public static int POINT_REDIS_GET_NUM_PER_TIME = 20;//每次从redis获取的最大数目

    //毫秒为单位
    public static int POINT_SAVE_IN_THREAD_POOL_TIME_INTERVAL = 60;//距离上次放入线程池的时间间隔
    public final static int POINT_GET_DATA_FROM_REDIS_MIN_TIME_INTERVAL = 20;//从redis最短取数据时间
    /**
     * 表示当前所操作的环境
     */
    public static String POINT_SAVE_ENV = PRO;

    private static boolean IS_DEBUG = false;


    public long mLastGetFromRedisTime;
    public long mLastPutDataInThreadPoolTime;

    @Value("${spring.profiles.active}")
    private String mEnv = "";

    private Map<String, List<UploadRecommendPointRequest<RecommendPoint>>> mTempBucketDataMaps = new HashMap<>();

    @Scheduled(fixedRate = POINT_GET_DATA_FROM_REDIS_MIN_TIME_INTERVAL)
    public void savePointsFromRedis() {
        if (EnvUtils.isEnvProd(mEnv) || IS_DEBUG) {
            if (IS_DEBUG) {
                POINT_SAVE_ENV = DEV;
                PointController.SAVE_POINT_QUEUES = "dev_point_save";
            }
            long lastOpearteTimes = (System.currentTimeMillis() - mLastGetFromRedisTime) / 1000;
            if (lastOpearteTimes > POINT_GET_DATA_FROM_REDIS_MIN_TIME_INTERVAL) {//大于上次取数间隔
                mLastGetFromRedisTime = System.currentTimeMillis();
                printLog(",lastTimePeriod-:" + lastOpearteTimes + "s;准备取出数据，redis中存储的数据总数：" + redisUtil.getQueueSize(RedisUtil.GROUP_RECOMMEND_POINT, PointController.SAVE_POINT_QUEUES));
                Object firstData = redisUtil.popQueueDataBlock(RedisUtil.GROUP_RECOMMEND_POINT, PointController.SAVE_POINT_QUEUES);
                List redisDatas = redisUtil.rangeQueueDataBlock(RedisUtil.GROUP_RECOMMEND_POINT, PointController.SAVE_POINT_QUEUES, POINT_REDIS_GET_NUM_PER_TIME);
                if (redisDatas != null && firstData != null) {
                    redisDatas.add(firstData);
                }
                printLog("取出的redis数据列表放入单个缓存队列中:" + (redisDatas != null ? redisDatas.size() : 0));
                if (redisDatas.size() > 0) {
                    putIntBuckets(redisDatas);
                    String saveCityCode = getSuitableCityCode();
                    Long checkStartTime = System.currentTimeMillis();
                    if (checkStartTime - mLastPutDataInThreadPoolTime > POINT_SAVE_IN_THREAD_POOL_TIME_INTERVAL) {
                        if (!StringUtils.isEmpty(saveCityCode)) {
                            printLog(";saveCityCode:" + saveCityCode + "dataSize:" + mTempBucketDataMaps.get(saveCityCode).size());
                            mLastPutDataInThreadPoolTime = System.currentTimeMillis();
                            savePointDataFromRedisInSingleThread(mTempBucketDataMaps.get(saveCityCode), saveCityCode);
                            mTempBucketDataMaps.put(saveCityCode, null);//置空队列中的数据
                            mLastGetFromRedisTime = System.currentTimeMillis();
                        }
                    } else {
                        printLog(";mLastPutDataInThreadPoolTime:" + mLastPutDataInThreadPoolTime + "与上次间隔:" + ((checkStartTime - mLastPutDataInThreadPoolTime) + "毫秒"));
                    }

                }
            } else {
                printLog("can  not get redis time，cur time:" + System.currentTimeMillis());
            }
        } else {
            logger.error("推荐点不处理");
        }
    }


    private void printLog(String log) {
        StringBuilder  res = new StringBuilder();
        res.append("loop_process_point_id:" + mLastGetFromRedisTime + ";");
        res.append(log);
        kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.WARN, res.toString());
        logger.info(res.toString());
    }

    private String getSuitableCityCode() {
        final String[] bingoCityCode = new String[1];
        mTempBucketDataMaps.forEach((k, v) -> {
            if (v != null && v.size() >= POINT_SAVE_BUCKET_SIZE) {
                bingoCityCode[0] = k;
            }
        });
        if (!StringUtils.isEmpty(bingoCityCode[0])) {
            return bingoCityCode[0];
        }
        return null;
    }

    private void putIntBuckets(List<UploadRecommendPointRequest<RecommendPoint>> redisDatas) {
        redisDatas.forEach(this::putInCache);
    }

    private void putInCache(UploadRecommendPointRequest<RecommendPoint> redisDatas) {
        if (mTempBucketDataMaps.get(redisDatas.getCityCode()) != null) {
            mTempBucketDataMaps.get(redisDatas.getCityCode()).add(redisDatas);
        } else {
            List<UploadRecommendPointRequest<RecommendPoint>> datas = new ArrayList<>();
            datas.add(redisDatas);
            mTempBucketDataMaps.put(redisDatas.getCityCode(), datas);
        }
    }

    public void savePointDataFromRedisInSingleThread(List<UploadRecommendPointRequest<RecommendPoint>> redisDatas, String cityCode) {
        pointRecommendService.batchSaveRecommendPoint(redisDatas, cityCode, POINT_SAVE_ENV);
    }
}
