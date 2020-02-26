package com.ruqi.appserver.ruqi.service;

import com.ruqi.appserver.ruqi.bean.UserEntity;
import com.ruqi.appserver.ruqi.dao.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

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
}
