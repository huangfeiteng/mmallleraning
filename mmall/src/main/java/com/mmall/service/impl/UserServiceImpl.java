package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
        User user = userMapper.selectUsernameAndPassword(username, md5Password);
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
        ServerResponse<String> validEmailResponse = this.checkValid(user.getEmail(), Const.EMAIL);
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

    @Override
    public ServerResponse<String> fogetGetQuestion(String userName) {
        ServerResponse<String> valisResponse = this.checkValid(userName, Const.USERNAME);
        if(valisResponse.isSuccess()){
            //用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String question = userMapper.selectForgetQuestion(userName);
        if(StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("用户提示问题不存在");
    }

    @Override
    public ServerResponse<String> checkAnswer(String userName, String question, String answer) {
        int resultNum = userMapper.checkAnswer(userName, question, answer);
        if(resultNum > 0){
            //说明问题及问题是这个用户的 并且正确
            String fogetToken = UUID.randomUUID().toString();
            TokenCache.setKey("foget_token"+userName,fogetToken);
            return ServerResponse.createBySuccessMessage(fogetToken);
        }
        return ServerResponse.createByErrorMessage("用户问题的答案错误");
    }

    @Override
    public ServerResponse<String> fogetResetPassword(String userName, String passwordNew, String token) {
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("参数错误，token为必传参数");
        }
        ServerResponse<String> validResponse = this.checkValid(userName, Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String fogetToken = TokenCache.getKey("foget_token" + userName);
        if(StringUtils.isBlank(fogetToken)){
            return ServerResponse.createByErrorMessage("token已失效或过期");
        }

        if(StringUtils.equals(token,fogetToken)){
            String MD5passwordNew = MD5Util.MD5EncodeUtf8(passwordNew);
           int resultNum =  userMapper.fogetResetPassWord(userName,MD5passwordNew);
           if(resultNum > 0){
               return ServerResponse.createBySuccessMessage("修改密码成功");
           }
        }else {
            return ServerResponse.createByErrorMessage("token校验失败，请重新获取密码问题的token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {
        int resultNum = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultNum == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int resultCount = userMapper.updateByPrimaryKeySelective(user);
        if(resultCount > 0){
            return ServerResponse.createBySuccessMessage("更新密码成功");
        }
        return ServerResponse.createBySuccessMessage("更新密码失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultNum = userMapper.checkEmailById(user.getEmail(),user.getId());
        if(resultNum > 0 ){
            return ServerResponse.createByErrorMessage("邮箱已经存在，请更换email重新更新");
        }
        User updateUser = new User();
        updateUser.setAnswer(user.getAnswer());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setId(user.getId());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0 ){
            return ServerResponse.createBySuccess("更新用户信息成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新用户信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        return ServerResponse.createBySuccess(user);
    }



    //backend

    /**
     * 校验用户是否是管理员角色
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){

            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
