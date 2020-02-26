package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.UserEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserMapper {
    @Select("SELECT * FROM user")
    @Results({@Result(property = "nickName", column = "nick_name") })
    List<UserEntity> getAll();

    @Select("SELECT * FROM users WHERE id = #{id}")
    @Results({@Result(property = "nickName", column = "nick_name") })
    UserEntity getOne(Long id);

    @Insert("insert into user(nick_name) values(#{nickName})")
    int insert(UserEntity user);

    @Update("UPDATE user SET userName=#{nick_name},nick_name=#{nickName} WHERE id =#{id}")
    void update(UserEntity user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    void delete(Long id);
}
