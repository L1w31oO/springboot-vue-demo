package com.lw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lw.entity.Book;

public interface BookMapper extends BaseMapper<Book> {  // BaseMapper<xx>针对xx的常用功能(CRUD...)
}
