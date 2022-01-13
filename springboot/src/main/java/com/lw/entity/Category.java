package com.lw.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@TableName("category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer pid;

    @TableField(exist = false)
    private List<Category> children;
}
