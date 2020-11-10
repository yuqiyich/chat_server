package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.RecommendPoint;
import com.ruqi.appserver.ruqi.controller.PointController;
import com.ruqi.appserver.ruqi.geomesa.RPHandleManager;
import com.ruqi.appserver.ruqi.kafka.BaseKafkaLogInfo;
import com.ruqi.appserver.ruqi.kafka.KafkaProducer;
import com.ruqi.appserver.ruqi.request.UploadRecommendPointRequest;
import com.ruqi.appserver.ruqi.utils.EnvUtils;
import com.ruqi.appserver.ruqi.utils.SpringContextUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruqi.appserver.ruqi.geomesa.RPHandleManager.DEV;
import static com.ruqi.appserver.ruqi.geomesa.RPHandleManager.PRO;


/**
 * 用来一点点的处理redis里面储存的数据
 */
@Component
public class PointSaveStartRunner implements CommandLineRunner {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    protected KafkaProducer kafkaProducer;

    @Autowired
    protected IPointRecommendService pointRecommendService;

    @Autowired
    RedisUtil redisUtil;

    ThreadPoolTaskExecutor executor;

    public static int POINT_SAVE_BUCKET_SIZE=60;
    public static int POINT_SAVE_TIME_INTERVAL=1*1000;//距离上次保存完数据的时间间隔
    /**
     * 表示当前所操作的环境
     */
    public static String POINT_SAVE_ENV = PRO;

    private static boolean IS_DEBUG = false;


    public  long mLastSaveTime;



    private Map<String, List<UploadRecommendPointRequest<RecommendPoint>>> mTempBucketDataMaps=new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
       if (EnvUtils.isEnvProd()||IS_DEBUG){
           if (IS_DEBUG) {
               POINT_SAVE_ENV = DEV;
           }
           executor = (ThreadPoolTaskExecutor) SpringContextUtils.getBean("taskExecutor");
           while (true){
               StringBuilder res =new StringBuilder( "准备取出数据，redis中存储的数据总数：" + redisUtil.getQueueSize(RedisUtil.GROUP_RECOMMEND_POINT, PointController.SAVE_POINT_QUEUES));
               Object  firstData= redisUtil.popQueueDataBlock(RedisUtil.GROUP_RECOMMEND_POINT, PointController.SAVE_POINT_QUEUES);
               List redisDatas= redisUtil.rangeQueueDataBlock(RedisUtil.GROUP_RECOMMEND_POINT, PointController.SAVE_POINT_QUEUES,POINT_SAVE_BUCKET_SIZE);
               if (redisDatas!=null&&firstData!=null){
                   redisDatas.add(firstData);
               }
               res.append("取出的redis数据列表放入单个缓存队列中:" + redisDatas!=null?redisDatas.size():0);
               if (redisDatas.size()>0){
                   putIntBuckets(redisDatas);
                   String saveCityCode = getSuitableCityCode();
                   if (!StringUtils.isEmpty(saveCityCode)){
                       res.append(";saveCityCode:"+saveCityCode+"dataSize:"+mTempBucketDataMaps.get(saveCityCode).size());
                       savePointDataFromRedisInSingleThread(mTempBucketDataMaps.get(saveCityCode),saveCityCode);
                       mTempBucketDataMaps.put(saveCityCode,null);//置空队列中的数据
                       mLastSaveTime = System.currentTimeMillis();
                   }
               }
               logger.info(res.toString());
               kafkaProducer.sendLog(BaseKafkaLogInfo.LogLevel.WARN,res.toString());
           }
       }

    }

    private String getSuitableCityCode() {
        Long checkStartTime=System.currentTimeMillis();
        final String[] bingoCityCode = new String[1];
        mTempBucketDataMaps.forEach((k,v)->{
            if (v != null && v.size() >= POINT_SAVE_BUCKET_SIZE) {
                bingoCityCode[0] = k;
            }
        });
        if (!StringUtils.isEmpty(bingoCityCode[0] )){
            return bingoCityCode[0];
        }
        if (checkStartTime - mLastSaveTime > POINT_SAVE_TIME_INTERVAL){
            mTempBucketDataMaps.forEach((k,v)->{
                if (v != null && v.size() > 0) {
                    bingoCityCode[0] = k;
                }
            });
            if (!StringUtils.isEmpty(bingoCityCode[0] )){
                return bingoCityCode[0];
            }
        }
        return null;
    }

    private void putIntBuckets(List<UploadRecommendPointRequest<RecommendPoint>> redisDatas) {
        redisDatas.forEach(this::putInCache);
    }

  private void  putInCache(UploadRecommendPointRequest<RecommendPoint> redisDatas){
        if (mTempBucketDataMaps.get(redisDatas.getCityCode())!=null){
            mTempBucketDataMaps.get(redisDatas.getCityCode()).add(redisDatas);
        } else {
            List<UploadRecommendPointRequest<RecommendPoint>> datas=new ArrayList<>();
            datas.add(redisDatas);
            mTempBucketDataMaps.put(redisDatas.getCityCode(),datas);
        }
    }

    @Async("taskExecutor")
    public void savePointDataFromRedisInSingleThread( List<UploadRecommendPointRequest<RecommendPoint>> redisDatas,String cityCode){
            RPHandleManager.getIns().batchSaveRecommendDatasByCityCode(POINT_SAVE_ENV, cityCode,  redisDatas);
    }
}
