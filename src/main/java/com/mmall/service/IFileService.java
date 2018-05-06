package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * mmall
 * com.mmall.service
 * Created by Zhang Chen
 * 2018/5/6
 */
public interface IFileService {

    public String upload(MultipartFile file, String path);

}
