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
 * mmall
 * com.mmall.service.impl
 * Created by Zhang Chen
 * 2018/5/6
 */

@Service("iFileService")
public class FileServiceImpl implements IFileService{

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtension;
        logger.info("开始上传文件，上传文件的文件名:{}, 上传的路径:{}, 新文件名:{}", fileName, path, uploadFileName);
        File fileDir = new File(path);
        if(!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        try {
            // file upload successful
            file.transferTo(targetFile);
            // upload to FTP server
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // after upload, delete uploaded file in tomcat server
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常",e);
        }
        return targetFile.getName();
    }

}
