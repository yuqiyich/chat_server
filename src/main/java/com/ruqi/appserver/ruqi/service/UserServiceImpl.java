package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.UserEntity;
import com.ruqi.appserver.ruqi.dao.entity.UserInfoEntity;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<UserEntity> findAll() {
        return userMapper.getAll();
    }

    @Override
    public UserEntity getOne(long id) {
        return userMapper.getOne(id);
    }

    @Override
    public int insert(UserEntity user) {
        return userMapper.insert(user);
    }

    @Override
    public void update(UserEntity user) {
        userMapper.update(user);
    }

    @Override
    public void delete(Long id) {
        userMapper.delete(id);
    }

    @Override
    public UserInfoEntity findUser(UserInfoEntity userInfoEntity) {
        return userMapper.findUser(userInfoEntity);
    }

    @Override
    public void updateAllUserToken() {
        userMapper.updateAllUserToken();
    }

    @Override
    public UserInfoEntity findUserByToken(String token) {
        return (UserInfoEntity) redisUtil.getKey(RedisUtil.GROUP_USER_INFO, token);
    }

}
