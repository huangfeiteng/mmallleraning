package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    String selectForgetQuestion(String userName);

    int checkAnswer(@Param("username") String userName,@Param("question") String question,@Param("answer") String answer);

    int fogetResetPassWord(@Param("username") String userName,@Param("passwordNew") String passwordNew);

    int checkPassword(@Param("password") String password,@Param("userId") Integer userId);

    int checkEmailById(@Param("email") String email , @Param("userId") Integer userId);
}