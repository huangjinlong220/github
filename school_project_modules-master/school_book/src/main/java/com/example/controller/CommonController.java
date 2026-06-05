package com.example.controller;

import cn.dev33.satoken.util.SaResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Api(tags = "通用接口")
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${file.path}")
    private String filePath;

    @ApiOperation(value = "上传图片", notes = "上传书籍封面图片，返回图片文件名")
    @PostMapping("/upload")
    public SaResult upload(
            @ApiParam(value = "上传的图片文件", required = true) @RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        String uploadDir = filePath;
        String originalFilename = file.getOriginalFilename();
        String newFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        File destFile = new File(uploadDir + newFilename);
        
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        
        try {
            file.transferTo(destFile);
            map.put("imageName", newFilename);
            return SaResult.data(map);
        } catch (IOException e) {
            e.printStackTrace();
            return SaResult.error("上传失败：" + e.getMessage());
        }
    }
}