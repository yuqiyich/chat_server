package com.ruqi.appserver.ruqi.dao.mappers;

import com.ruqi.appserver.ruqi.bean.UserEntity;
import com.ruqi.appserver.ruqi.dao.entity.UserInfoEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    @Select("SELECT * FROM risk_user")
    @Results({@Result(property = "nickName", column = "nick_name")})
    List<UserEntity> getAll();

    @Select("SELECT * FROM risk_user WHERE user_id = #{userId} AND app_id = #{appId}")
    @Results({@Result(property = "userId", column = "user_id"), @Result(property = "appId", column = "app_id")})
    UserEntity getOne(@Param("userId") Long userId, int appId);

    @Insert("insert into risk_user(user_id,user_phone,nick_name,app_id) values(#{userId},#{userPhone},#{nickName},#{appId})")
    int insert(UserEntity user);

    @Update("UPDATE risk_user SET nick_name=#{nickName},user_phone=#{userPhone} WHERE user_id =#{userId} AND app_id=#{appId}")
    void update(UserEntity user);

//    @Delete("DELETE FROM risk_user WHERE user_id =#{id}")
//    void delete(Long id);

    @Select("SELECT * FROM user WHERE account = #{account} AND password = #{password}")
    @Results({@Result(property = "userStatus", column = "user_status")})
    UserInfoEntity findUser(UserInfoEntity userInfoEntity);

    // 简单实现，[0, 1) + id ,MD5，则每个user的token都会不一样，每次更新后token也基本都会变化
    @Update("UPDATE user SET token=Md5(rand() + id) WHERE user_status=1")
    void updateAllUserToken();

}
