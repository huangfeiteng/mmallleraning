package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Description:
 *
 * @author Huangfeiteng
 * @date Created on 2020/5/8
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    private static Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        //上传的文件名
        String fileName = file.getOriginalFilename();
        //获取文件扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        //上传后新的文件名
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名：{}，上传的路径：{}，上传后新的文件名：{}",fileName,path,uploadFileName);

        //创建文件夹
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.setWritable(true);
            dirFile.mkdirs();
        }

        //目标文件file流
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //文件已经上传成功了

            //将targetFile上传到ftp服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));

            //上传ftp成功后 删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error(
                    "上传文件失败",e
            );

            return null;
        }

        return targetFile.getName();

    }
}
