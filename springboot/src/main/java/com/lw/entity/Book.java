package com.lw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@TableName("book")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private BigDecimal price;
    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT + 8")
    private Date createTime;
    private String cover;
}
