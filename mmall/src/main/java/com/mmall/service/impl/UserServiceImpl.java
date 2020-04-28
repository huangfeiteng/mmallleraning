package com.mmall.service.impl;

import com.mmall.common.Const;
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
        ServerResponse<String> validUsernameResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validUsernameResponse.isSuccess()){
            return validUsernameResponse;
        }
        //校验邮箱
        ServerResponse<String> validEmailResponse = this.checkValid(user.getUsername(), Const.EMAIL);
        if (!validEmailResponse.isSuccess()){
            return validEmailResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultRow = userMapper.insert(user);

        if (resultRow == 0 ){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createBySuccessMessage("注册成功");

    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {

        if (StringUtils.isNoneBlank(type)){
            if (Const.USERNAME.equals(type)){
                //校验用户名
                int resultNum = userMapper.checkUsername(str);
                if(resultNum > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)){
                //校验邮箱
                int checkEmailResult = userMapper.checkEmail(str);
                if(checkEmailResult > 0 ){
                    return ServerResponse.createByErrorMessage("邮箱已经被注册");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }
}
