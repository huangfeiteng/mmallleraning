package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;
import java.util.Set;


/**
 * Description:
 *
 * @author Huangfeiteng
 * @date Created on 2020/5/3
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createBySuccessMessage("添加分类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        int resultNum = categoryMapper.insert(category);
        if(resultNum > 0){
            return ServerResponse.createBySuccessMessage("添加分类成功");
        }

        return ServerResponse.createByErrorMessage("添加分类失败");

    }

    public ServerResponse updateCategoryName(String categoryName,Integer categoryId){

        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createBySuccessMessage("更新分类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int resultNum = categoryMapper.updateByPrimaryKeySelective(category);
        if(resultNum > 0 ){
            return ServerResponse.createBySuccessMessage("更新分类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新分类名称失败");

    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){

        //获取当前分类的子分类 不递归
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("查询当前分类的子分类为空");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


    /**
     * 递归查询本节点id以及子节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        this.findChildrenCategory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();

        if(categoryId != null){

            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }

        return ServerResponse.createBySuccess(categoryIdList);


    }

    private Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        Category categoryCurrent = categoryMapper.selectByPrimaryKey(categoryId);
        if(categoryCurrent != null){
            categorySet.add(categoryCurrent);
        }
        //查询当前节点的子节点list
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
        for (Category categoryItem:categoryList) {
            findChildrenCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
