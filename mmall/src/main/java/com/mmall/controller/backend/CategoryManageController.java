package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Description:
 *
 * @author Huangfeiteng
 * @date Created on 2020/5/3
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session,String categoryName,@RequestParam(value = "parentId",defaultValue = "0") int parentId){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要先进行登录");
        }
        //校验是否是管理员角色
        ServerResponse serverResponse = iUserService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            //是管理员角色 进行分类的相关操作
            return iCategoryService.addCategory(categoryName, parentId);

        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限进行操作");
        }
    }


    @RequestMapping(value = "set_categoryName.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session ,String categoryName,Integer categoryId){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要先进行登录");
        }
        //校验是否是管理员角色
        ServerResponse serverResponse = iUserService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            return iCategoryService.updateCategoryName(categoryName,categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限进行操作");
        }
    }


    @RequestMapping(value = "get_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session,Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要先进行登录");
        }
        //校验是否是管理员角色
        ServerResponse serverResponse = iUserService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
           return iCategoryService.getChildrenParallelCategory(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限进行操作");
        }
    }

    @RequestMapping(value = "get_deep_category.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session,Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"需要先进行登录");
        }
        //校验是否是管理员角色
        ServerResponse serverResponse = iUserService.checkAdminRole(user);
        if(serverResponse.isSuccess()){
            //查询当前节点的id和递归子节点的id 0-》1000-》10000
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限进行操作");
        }
    }

}
