package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultNum =  userMapper.checkUsername(username);
        if(resultNum == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //md5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectUsernameAndPassword(username, password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码有误，请核实");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    @Override
    public ServerResponse<String> register(User user){
        //校验用户名
        int resultNum = userMapper.checkUsername(user.getUsername());
        if(resultNum > 0){
            return ServerResponse.createByErrorMessage("用户名已存在");
        }
        //校验邮箱
        int checkEmailResult = userMapper.checkEmail(user.getEmail());
        if(checkEmailResult > 0 ){
            return ServerResponse.createByErrorMessage("邮箱已经被注册");
        }
        //todo

        return null;

    }
}
