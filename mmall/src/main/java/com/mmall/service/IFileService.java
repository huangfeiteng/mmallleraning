package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Description:
 *
 * @author Huangfeiteng
 * @date Created on 2020/5/8
 */
public interface IFileService {
    String upload(MultipartFile file, String path);
}
