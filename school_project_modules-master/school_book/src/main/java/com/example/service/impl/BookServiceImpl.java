package com.example.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.controller.UserEntityControllerParent;
import com.example.entity.BookEntity;
import com.example.mapper.BookMapper;
import com.example.service.BookService;
import com.example.utils.FileUrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl extends ServiceImpl<BookMapper, BookEntity> implements BookService {

    @Autowired(required = false)
    private UserEntityControllerParent userEntityControllerParent;

    @Override
    public SaResult getBookPage(Integer pageNum, Integer pageSize, Integer type, String keyword) {
        Page<BookEntity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(type != null, BookEntity::getType, type)
                .and(StringUtils.hasText(keyword), w -> w.like(BookEntity::getTitle, keyword)
                        .or().like(BookEntity::getAuthor, keyword))
                .orderByDesc(BookEntity::getSubmitTime);
        
        Page<BookEntity> bookPage = this.page(page, queryWrapper);
        
        List<BookEntity> records = bookPage.getRecords().stream()
                .map(book -> {
                    book.setImageUrl(FileUrlUtils.getImageUrl(book.getImage()));
                    return book;
                })
                .collect(Collectors.toList());
        
        bookPage.setRecords(records);
        return SaResult.data(bookPage);
    }

    @Override
    public SaResult getRecommendList(Integer limit) {
        LambdaQueryWrapper<BookEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookEntity::getIsRecommend, 1)
                .orderByDesc(BookEntity::getSortOrder)
                .orderByDesc(BookEntity::getSubmitTime)
                .last("LIMIT " + limit);
        
        List<BookEntity> list = this.list(queryWrapper);
        list = list.stream()
                .map(book -> {
                    book.setImageUrl(FileUrlUtils.getImageUrl(book.getImage()));
                    return book;
                })
                .collect(Collectors.toList());
        
        return SaResult.data(list);
    }

    @Override
    public SaResult getBannerList(Integer limit) {
        LambdaQueryWrapper<BookEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookEntity::getIsBanner, 1)
                .orderByDesc(BookEntity::getSortOrder)
                .orderByDesc(BookEntity::getSubmitTime)
                .last("LIMIT " + limit);
        
        List<BookEntity> list = this.list(queryWrapper);
        list = list.stream()
                .map(book -> {
                    book.setImageUrl(FileUrlUtils.getImageUrl(book.getImage()));
                    return book;
                })
                .collect(Collectors.toList());
        
        return SaResult.data(list);
    }

    @Override
    public SaResult getHotList(Integer limit) {
        LambdaQueryWrapper<BookEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(BookEntity::getViewCount)
                .orderByDesc(BookEntity::getSubmitTime)
                .last("LIMIT " + limit);
        
        List<BookEntity> list = this.list(queryWrapper);
        list = list.stream()
                .map(book -> {
                    book.setImageUrl(FileUrlUtils.getImageUrl(book.getImage()));
                    return book;
                })
                .collect(Collectors.toList());
        
        return SaResult.data(list);
    }

    @Override
    public SaResult getBookDetail(Long id) {
        if (id == null) {
            return SaResult.error("书籍ID不能为空");
        }
        
        BookEntity book = this.getById(id);
        if (book == null) {
            return SaResult.error("书籍不存在");
        }
        
        this.update()
                .setSql("view_count = IFNULL(view_count, 0) + 1")
                .eq("id", id)
                .update();
        
        book.setImageUrl(FileUrlUtils.getImageUrl(book.getImage()));
        return SaResult.data(book);
    }

    @Override
    public SaResult addBook(BookEntity book) {
        book.setSubmitTime(LocalDateTime.now());
        boolean success = this.save(book);
        if (success) {
            return SaResult.ok("书籍添加成功");
        }
        return SaResult.error("书籍添加失败");
    }

    @Override
    public SaResult updateBook(BookEntity book) {
        if (book.getId() == null) {
            return SaResult.error("书籍ID不能为空");
        }
        
        BookEntity existBook = this.getById(book.getId());
        if (existBook == null) {
            return SaResult.error("书籍不存在");
        }
        
        boolean success = this.updateById(book);
        if (success) {
            return SaResult.ok("书籍修改成功");
        }
        return SaResult.error("书籍修改失败");
    }

    @Override
    public SaResult deleteBook(Long id) {
        if (id == null) {
            return SaResult.error("书籍ID不能为空");
        }
        
        BookEntity existBook = this.getById(id);
        if (existBook == null) {
            return SaResult.error("书籍不存在");
        }
        
        boolean success = this.removeById(id);
        if (success) {
            return SaResult.ok("书籍删除成功");
        }
        return SaResult.error("书籍删除失败");
    }

    @Override
    public SaResult saveBook(BookEntity book) {
        if (book.getSubmitTime() == null) {
            book.setSubmitTime(LocalDateTime.now());
        }
        if (book.getSubmitUser() == null && StpUtil.isLogin()) {
            String phone = (String) StpUtil.getLoginId();
            Long userId = userEntityControllerParent.selectByPhone(phone);
            book.setSubmitUser(userId);
        }
        boolean success = this.save(book);
        return success ? SaResult.ok("保存成功") : SaResult.error("保存失败");
    }

    @Override
    public List<String> getImagesNames() {
        List<String> list = new ArrayList<>();
        List<BookEntity> books = this.list();
        for (BookEntity book : books) {
            if (book.getImage() != null) {
                list.add(book.getImage());
            }
        }
        return list;
    }
}