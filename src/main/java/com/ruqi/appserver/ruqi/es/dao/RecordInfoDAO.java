//package com.ruqi.appserver.ruqi.es.dao;
//
//import com.ruqi.appserver.ruqi.bean.RecordInfo;
//import org.elasticsearch.client.ElasticsearchClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.data.elasticsearch.annotations.Document;
//import org.springframework.data.repository.CrudRepository;
//
//import java.util.Optional;
//
//public class RecordInfoDAO implements CrudRepository<RecordInfo, String> {
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Override
//    public <S extends RecordInfo> S save(S s) {
////        return new ElasticsearchClient().;
//        logger.info("--->save s=" + s);
//        return s;
//    }
//
//    @Override
//    public <S extends RecordInfo> Iterable<S> saveAll(Iterable<S> iterable) {
//        return null;
//    }
//
//    @Override
//    public Optional<RecordInfo> findById(String s) {
//        logger.info("--->findById " + s);
//        return Optional.empty();
//    }
//
//    @Override
//    public boolean existsById(String s) {
//        return false;
//    }
//
//    @Override
//    public Iterable<RecordInfo> findAll() {
//        return null;
//    }
//
//    @Override
//    public Iterable<RecordInfo> findAllById(Iterable<String> iterable) {
//        return null;
//    }
//
//    @Override
//    public long count() {
//        return 0;
//    }
//
//    @Override
//    public void deleteById(String s) {
//
//    }
//
//    @Override
//    public void delete(RecordInfo recordInfo) {
//
//    }
//
//    @Override
//    public void deleteAll(Iterable<? extends RecordInfo> iterable) {
//
//    }
//
//    @Override
//    public void deleteAll() {
//
//    }
//}
