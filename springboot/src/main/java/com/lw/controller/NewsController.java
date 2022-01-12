package com.lw.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lw.common.Result;
import com.lw.entity.News;
import com.lw.mapper.NewsMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Resource
    private NewsMapper newsMapper;
    
    @PostMapping
    public Result<?> save(@RequestBody News news) {
        news.setTime(new Date());
        newsMapper.insert(news);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody News news) {  // @RequestBody将前端传来的Json对象映射为News实体
        newsMapper.updateById(news);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        newsMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(newsMapper.selectById(id));
    }

    @GetMapping("")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        // 页面属性
        Page<News> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<News> wrapper = Wrappers.<News>lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(News::getTitle, search);
        }
        // 页面信息
        Page<News> newsPage = newsMapper.selectPage(page, wrapper);
        return Result.success(newsPage);
    }
}
