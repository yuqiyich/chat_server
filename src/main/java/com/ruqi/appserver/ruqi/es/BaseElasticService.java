//package com.ruqi.appserver.ruqi.es;
//
//import com.alibaba.fastjson.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.indices.CreateIndexRequest;
//import org.elasticsearch.client.indices.CreateIndexResponse;
//import org.elasticsearch.client.indices.GetIndexRequest;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class BaseElasticService {
//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    RestHighLevelClient restHighLevelClient;
//
////    /**
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:30
////     * @param idxName   索引名称
////     * @param idxSQL    索引描述
////     * @return void
////     * @throws
////     * @since
////     */
////    public void createIndex(String idxName,String idxSQL){
////        try {
////            if (!this.indexExist(idxName)) {
////                logger.error(" idxName={} 已经存在,idxSql={}",idxName,idxSQL);
////                return;
////            }
////            CreateIndexRequest request = new CreateIndexRequest(idxName);
////            buildSetting(request);
////            request.mapping(idxSQL, XContentType.JSON);
//////            request.settings() 手工指定Setting
////            CreateIndexResponse res = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
////            if (!res.isAcknowledged()) {
////                throw new RuntimeException("初始化失败");
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////            System.exit(0);
////        }
////    }
////
////    /** 断某个index是否存在
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:27
////     * @param idxName index名
////     * @return boolean
////     * @throws
////     * @since
////     */
////    public boolean indexExist(String idxName) throws Exception {
////        GetIndexRequest request = new GetIndexRequest(idxName);
////        request.local(false);
////        request.humanReadable(true);
////        request.includeDefaults(false);
////        request.indicesOptions(IndicesOptions.lenientExpandOpen());
////        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
////    }
////
////    /** 断某个index是否存在
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:27
////     * @param idxName index名
////     * @return boolean
////     * @throws
////     * @since
////     */
////    public boolean isExistsIndex(String idxName) throws Exception {
////        return restHighLevelClient.indices().exists(new GetIndexRequest(idxName),RequestOptions.DEFAULT);
////    }
////
////    /** 设置分片
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 19:27
////     * @param request
////     * @return void
////     * @throws
////     * @since
////     */
////    public void buildSetting(CreateIndexRequest request){
////        request.settings(Settings.builder().put("index.number_of_shards",3)
////                .put("index.number_of_replicas",2));
////    }
//    /**
//     * @author WCNGS@QQ.COM
//     * @See
//     * @date 2019/10/17 17:27
//     * @param idxName index
//     * @param entity    对象
//     * @return void
//     * @throws
//     * @since
//     */
//    public void insertOrUpdateOne(String idxName, ElasticEntity entity) {
//        IndexRequest request = new IndexRequest(idxName);
//        logger.error("Data : id={},entity={}",entity.getId(), JSON.toJSONString(entity.getData()));
//        request.id(entity.getId());
//        request.source(entity.getData(), XContentType.JSON);
////        request.source(JSON.toJSONString(entity.getData()), XContentType.JSON);
//        try {
//            restHighLevelClient.index(request, RequestOptions.DEFAULT);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
////
////    /** 批量插入数据
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:26
////     * @param idxName index
////     * @param list 带插入列表
////     * @return void
////     * @throws
////     * @since
////     */
////    public void insertBatch(String idxName, List<ElasticEntity> list) {
////        BulkRequest request = new BulkRequest();
////        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getId())
////                .source(item.getData(), XContentType.JSON)));
////        try {
////            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////    /** 批量删除
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:14
////     * @param idxName index
////     * @param idList    待删除列表
////     * @return void
////     * @throws
////     * @since
////     */
////    public <T> void deleteBatch(String idxName, Collection<T> idList) {
////        BulkRequest request = new BulkRequest();
////        idList.forEach(item -> request.add(new DeleteRequest(idxName, item.toString())));
////        try {
////            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////    /**
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:14
////     * @param idxName index
////     * @param builder   查询参数
////     * @param c 结果类对象
////     * @return java.util.List<T>
////     * @throws
////     * @since
////     */
////    public <T> List<T> search(String idxName, SearchSourceBuilder builder, Class<T> c) {
////        SearchRequest request = new SearchRequest(idxName);
////        request.source(builder);
////        try {
////            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
////            SearchHit[] hits = response.getHits().getHits();
////            List<T> res = new ArrayList<>(hits.length);
////            for (SearchHit hit : hits) {
////                res.add(JSON.parseObject(hit.getSourceAsString(), c));
////            }
////            return res;
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////    /** 删除index
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:13
////     * @param idxName
////     * @return void
////     * @throws
////     * @since
////     */
////    public void deleteIndex(String idxName) {
////        try {
////            if (!this.indexExist(idxName)) {
////                log.error(" idxName={} 已经存在",idxName);
////                return;
////            }
////            restHighLevelClient.indices().delete(new DeleteIndexRequest(idxName), RequestOptions.DEFAULT);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
////
////
////    /**
////     * @author WCNGS@QQ.COM
////     * @See
////     * @date 2019/10/17 17:13
////     * @param idxName
////     * @param builder
////     * @return void
////     * @throws
////     * @since
////     */
////    public void deleteByQuery(String idxName, QueryBuilder builder) {
////
////        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
////        request.setQuery(builder);
////        //设置批量操作数量,最大为10000
////        request.setBatchSize(10000);
////        request.setConflicts("proceed");
////        try {
////            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
//}
