package com.lw.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lw.common.Result;
import com.lw.entity.User;
import com.lw.mapper.UserMapper;

import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {  // @RequestBody将前端传来的Json对象映射为User实体
        // 前端登录传来的用户名
        String currentUsername = user.getUsername();
        // 前端登录传来的用户密码
        String currentPassword = user.getPassword();
        // 与数据库中的用户数据对比
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, currentUsername).eq(User::getPassword, currentPassword);
        User selectUser = userMapper.selectOne(wrapper);
        if (selectUser == null) {
            return Result.error("-1", "用户名或密码错误！");
        }
        return Result.success(selectUser);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        // 前端注册传来的用户名
        String inputUsername = user.getUsername();
        // 与数据库中的用户名对比
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, inputUsername);
        User selectUser = userMapper.selectOne(wrapper);
        if (selectUser != null) {
            return Result.error("-1", "用户名重复！");
        }
        if (user.getPassword() == null){
            user.setPassword("123456");
        }
        userMapper.insert(user);
        return Result.success();
    }

    @PostMapping
    public Result<?> save(@RequestBody User user) {
        if (user.getPassword() == null){
            user.setPassword("123456");
        }
        userMapper.insert(user);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody User user) {  // @RequestBody将前端传来的Json对象映射为User实体
        userMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(userMapper.selectById(id));
    }

    @GetMapping("")
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(defaultValue = "") String search) {
        // 页面属性
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery();
        if (StrUtil.isNotBlank(search)) {
            wrapper.like(User::getNickName, search);
        }
        // 页面信息
        Page<User> userPage = userMapper.selectPage(page, wrapper);
        return Result.success(userPage);
    }
}
