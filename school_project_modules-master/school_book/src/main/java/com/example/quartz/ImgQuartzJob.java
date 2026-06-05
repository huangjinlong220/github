package com.example.quartz;

import com.example.service.BookService;
import com.example.utils.FileUrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ImgQuartzJob implements Job {

    private final BookService bookService;

    private final FileUrlUtils fileUtil;

    @Value("${file.path}")
    private String filePath;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<String> imagesNames = bookService.getImagesNames();
            if (imagesNames.isEmpty()) return;
            List<String> imgs = fileUtil.listFileNames(filePath);
            if (imgs.isEmpty()) return;
            List<String> difference = new ArrayList<>(imgs);
            difference.removeAll(imagesNames);
            for (String name : difference) {
                fileUtil.deleteFile(filePath + name);
                log.info("删除了文件名: " + name);
            }
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}