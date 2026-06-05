package com.example.controller;

import cn.dev33.satoken.util.SaResult;
import com.example.entity.BookEntity;
import com.example.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "书籍接口")
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @ApiOperation(value = "分页获取书籍列表", notes = "分页查询所有书籍，支持按分类筛选和关键词搜索")
    @GetMapping("/list")
    public SaResult getBookList(
            @ApiParam(value = "页码，默认1") @RequestParam(defaultValue = "1", required = false) Integer pageNum,
            @ApiParam(value = "每页数量，默认10") @RequestParam(defaultValue = "10", required = false) Integer pageSize,
            @ApiParam(value = "分类（1:编程 2:文学 3:科技 4:历史 5:其他）") @RequestParam(value = "type", required = false) Integer type,
            @ApiParam(value = "搜索关键词，可搜索书名或作者") @RequestParam(value = "keyword", required = false) String keyword) {
        return bookService.getBookPage(pageNum, pageSize, type, keyword);
    }

    @ApiOperation(value = "获取推荐书籍列表", notes = "获取推荐书籍，按sort_order和submit_time排序")
    @GetMapping("/recommend")
    public SaResult getRecommendList(
            @ApiParam(value = "返回数量，默认10") @RequestParam(defaultValue = "10", required = false) Integer limit) {
        return bookService.getRecommendList(limit);
    }

    @ApiOperation(value = "获取轮播图列表", notes = "获取轮播图书籍，按sort_order和submit_time排序")
    @GetMapping("/banner")
    public SaResult getBannerList(
            @ApiParam(value = "返回数量，默认5") @RequestParam(defaultValue = "5", required = false) Integer limit) {
        return bookService.getBannerList(limit);
    }

    @ApiOperation(value = "获取热门书籍列表", notes = "获取热门书籍，按view_count和submit_time排序")
    @GetMapping("/hot")
    public SaResult getHotList(
            @ApiParam(value = "返回数量，默认10") @RequestParam(defaultValue = "10", required = false) Integer limit) {
        return bookService.getHotList(limit);
    }

    @ApiOperation(value = "根据ID获取书籍详情", notes = "获取书籍详情，同时浏览次数+1")
    @GetMapping("/{id}")
    public SaResult getBookDetail(
            @ApiParam(value = "书籍ID", required = true) @PathVariable Long id) {
        return bookService.getBookDetail(id);
    }

    @ApiOperation(value = "添加书籍", notes = "添加新书籍")
    @PostMapping
    public SaResult addBook(@RequestBody BookEntity book) {
        return bookService.addBook(book);
    }

    @ApiOperation(value = "修改书籍", notes = "修改书籍信息")
    @PutMapping
    public SaResult updateBook(@RequestBody BookEntity book) {
        return bookService.updateBook(book);
    }

    @ApiOperation(value = "删除书籍", notes = "根据ID删除书籍")
    @DeleteMapping("/{id}")
    public SaResult deleteBook(
            @ApiParam(value = "书籍ID", required = true) @PathVariable Long id) {
        return bookService.deleteBook(id);
    }

    @ApiOperation(value = "保存书籍信息", notes = "保存书籍到数据库。image字段填入上传图片接口返回的imageName；submitTime不传则自动设为当前时间")
    @PostMapping("/save")
    public SaResult saveBook(
            @ApiParam(value = "书籍实体，image字段填入上传图片接口返回的imageName", required = true) @RequestBody BookEntity book) {
        return bookService.saveBook(book);
    }
}