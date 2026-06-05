package com.example.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class FileUrlUtils {

    private static String urlPrefix;

    @Value("${file.url-prefix:/images/}")
    public void setUrlPrefix(String urlPrefix) {
        FileUrlUtils.urlPrefix = urlPrefix;
    }

    public static String getImageUrl(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            return null;
        }
        if (imageName.startsWith("http://") || imageName.startsWith("https://") || imageName.startsWith("/images/")) {
            return imageName;
        }
        return urlPrefix + imageName;
    }

    public List<String> listFileNames(String dirPath) {
        if (dirPath == null || dirPath.isEmpty()) {
            return List.of();
        }
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return List.of();
        }
        String[] fileArray = dir.list();
        if (fileArray == null) {
            return List.of();
        }
        return Arrays.asList(fileArray);
    }

    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }
}