package com.mmall.service.impl;

import com.mmall.common.ServerResponse;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Product;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author Huangfeiteng
 * @date Created on 2020/5/6
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse saveOrUpdateProduct(Product product){
        if(product == null){
            return ServerResponse.createByErrorMessage("新增或更新商品参数错误");
        }

        if(StringUtils.isNoneBlank(product.getSubImages())){
            String[] subImages = product.getSubImages().split(",");
            if (subImages.length > 0){
                product.setMainImage(subImages[0]);
            }
        }

        if (product.getId() != null){
            //更新
            int resultNum = productMapper.updateByPrimaryKeySelective(product);
            if (resultNum > 0){
                return ServerResponse.createBySuccessMessage("更新商品信息成功");
            }
            return ServerResponse.createBySuccessMessage("更新商品信息失败");
        }else {
            //新增
            int resultNum = productMapper.insert(product);
            if(resultNum > 0){
                return ServerResponse.createBySuccessMessage("新增商品信息成功");
            }
            return ServerResponse.createBySuccessMessage("新增商品信息失败");
        }
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){

        if(productId == null || status == null){
            return ServerResponse.createByErrorMessage("修改销售状态入参错误。");
        }

        Product productNew = new Product();
        productNew.setId(productId);
        productNew.setStatus(status);

        int resultNum = productMapper.updateByPrimaryKeySelective(productNew);
        if(resultNum > 0){
            return ServerResponse.createBySuccessMessage("修改商品销售状态成功");
        }

        return ServerResponse.createByErrorMessage("修改商品销售状态失败");
    }
}
