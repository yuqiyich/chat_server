package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.UserEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserMapper {
    @Select("SELECT * FROM risk_user")
    @Results({@Result(property = "nickName", column = "nick_name") })
    List<UserEntity> getAll();

    @Select("SELECT * FROM risk_user WHERE user_id = #{userId}")
    @Results({@Result(property = "nickName", column = "nick_name") })
    UserEntity getOne(Long id);

    @Insert("insert into risk_user(user_id,user_phone,nick_name,user_name) values(#{userId},#{userPhone},#{nickName},#{userName})")
    int insert(UserEntity user);

    @Update("UPDATE risk_user SET user_name=#{userName},nick_name=#{nickName} WHERE user_id =#{userId}")
    void update(UserEntity user);

    @Delete("DELETE FROM risk_user WHERE user_id =#{id}")
    void delete(Long id);
}
