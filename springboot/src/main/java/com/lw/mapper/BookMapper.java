package com.lw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lw.entity.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BookMapper extends BaseMapper<Book> {  // BaseMapper<xx>针对xx的常用功能(CRUD...)
    // 根据用户id查询图书信息
    List<Book> findByUserId(@Param("userId") Integer userId);
}
