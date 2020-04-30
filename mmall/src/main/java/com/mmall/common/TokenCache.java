package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author Huangfeiteng
 * @date Created on 2020/4/30
 */
public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    //LRU
    private static LoadingCache<String,String> loadCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
            .expireAfterAccess(12, TimeUnit.HOURS).build(new CacheLoader<String, String>() {
                @Override
                //当调用get获取不到值时，如果key没有对应的值时会执行此方法
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        loadCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = loadCache.get(key);
            if("null".equals(value)) {
                return null;
            }
        }catch (Exception ex){

            logger.error("localCache getkey error",ex);
        }
        return value;
    }
}
