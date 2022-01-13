package com.lw.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lw.common.Result;
import com.lw.entity.Book;
import com.lw.mapper.BookMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController extends BaseController {

    @Resource
    private BookMapper bookMapper;
    
    @PostMapping
    public Result<?> save(@RequestBody Book book) {
        bookMapper.insert(book);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Book book) {  // @RequestBody将前端传来的Json对象映射为Book实体
        bookMapper.updateById(book);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bookMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @PostMapping("/deleteBatch")
    public Result<?> deleteBatch(@RequestBody List<Integer> ids) {
        bookMapper.deleteBatchIds(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(bookMapper.selectById(id));
    }

    /**
     * 注意：这个方法使用的是Mybatis sql方式做的多表联合查询
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public Result<?> getByUserId(@PathVariable Integer userId) {
        return Result.success(bookMapper.findByUserId(userId));
    }

    @GetMapping("")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        // 页面属性
        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = Wrappers.<Book>lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(Book::getName, search);
        }
        // 页面信息
        Page<Book> bookPage = bookMapper.selectPage(page, wrapper);
        return Result.success(bookPage);
    }
}
