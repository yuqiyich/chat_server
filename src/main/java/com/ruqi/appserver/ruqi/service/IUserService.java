package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.UserEntity;
import com.ruqi.appserver.ruqi.dao.entity.UserInfoEntity;

import java.util.List;

/**
 * 用户交互接口
 */

public interface IUserService {
    List<UserEntity> findAll();

    UserEntity getOne(long id);

    int insert(UserEntity user);

    void update(UserEntity user);

    void delete(Long id);

    UserInfoEntity findUser(UserInfoEntity userInfoEntity);

    void updateAllUserToken();

    UserInfoEntity findUserByToken(String token);
}
