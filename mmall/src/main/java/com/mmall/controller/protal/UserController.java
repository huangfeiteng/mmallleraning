package com.mmall.controller.protal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录接口
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "login.do" , method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username , String password, HttpSession session) {

        ServerResponse<User> userServerResponse = iUserService.login(username, password);
        if(userServerResponse.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,userServerResponse.getData());
        }
        return userServerResponse;
    }

    /**
     * 登出功能
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do" , method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册功能
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    /**
     * 校验用户名和邮箱是否存在
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> checkValid(String str , String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "get_userinfo.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getCurrentUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户");
    }


    @RequestMapping(value = "foget_get_question.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> fogetGetQuestion(String userName){

        return iUserService.fogetGetQuestion(userName);
    }


    @RequestMapping(value = "foget_check_answer.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> fogetCheckAnswer(String userName ,String question ,String answer){
        return iUserService.checkAnswer(userName,question,answer);
    }
}
