package com.lw.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lw.common.Result;
import com.lw.entity.User;
import com.lw.mapper.UserMapper;

import com.lw.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<?> login(@RequestBody User user) {  // @RequestBody将前端传来的Json对象映射为User实体

        // 前端登录传来的用户名
        String currentUsername = user.getUsername();
        // 前端登录传来的用户密码
        String currentPassword = user.getPassword();
        // 前端登录传来的用户权限
        Integer currentRole = user.getRole();

        // 与数据库中的用户数据对比
//        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery()
//                .eq(User::getUsername, currentUsername)
//                .eq(User::getPassword, currentPassword)
//                .eq(User::getRole, role);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", currentUsername);
        queryWrapper.eq("password", currentPassword);
        queryWrapper.eq("role", currentRole);
        User selectedUser = userMapper.selectOne(queryWrapper);
        if (selectedUser == null) {
            return Result.error("-1", "用户名或密码错误！");
        }
        // 生成token
        String token = TokenUtils.genToken(selectedUser);
        selectedUser.setToken(token);
        return Result.success(selectedUser);
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
        user.setRole(2);
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
//        Page<User> userPage = userMapper.selectPage(page, wrapper);
//        Page<User> userPage = userMapper.findPage(new Page<>(pageNum, pageSize));
        Page<User> userPage = userMapper.findPage(new Page<>(pageNum, pageSize), search);
        return Result.success(userPage);
    }

    @GetMapping("/all")
    public Result<?> findAll() {
        return Result.success(userMapper.selectList(null));
    }

    /**
     * Excel导出
     * @param response
     * @throws IOException
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {

        List<Map<String, Object>> list = CollUtil.newArrayList();

        List<User> all = userMapper.selectList(null);
        for (User user : all) {
            Map<String, Object> row1 = new LinkedHashMap<>();
            row1.put("用户名", user.getUsername());
            row1.put("昵称", user.getNickName());
            row1.put("年龄", user.getAge());
            row1.put("性别", user.getSex());
            row1.put("地址", user.getAddress());
            row1.put("角色", user.getRole());
            list.add(row1);
        }

        // 2. 写excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(list, true);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("用户信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        writer.close();
        IoUtil.close(System.out);
    }

    /**
     * Excel导入
     * 导入的模板可以使用 Excel导出的文件
     * @param file Excel
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    public Result<?> upload(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        List<List<Object>> lists = ExcelUtil.getReader(inputStream).read(1);
        List<User> saveList = new ArrayList<>();
        for (List<Object> row : lists) {
            User user = new User();
            user.setUsername(row.get(0).toString());
            user.setNickName(row.get(1).toString());
            user.setAge(Integer.valueOf(row.get(2).toString()));
            user.setSex(row.get(3).toString());
            user.setAddress(row.get(4).toString());
            user.setRole(Integer.valueOf(row.get(5).toString()));
            saveList.add(user);
        }
        for (User user : saveList) {
            userMapper.insert(user);
        }
        return Result.success();
    }
    /**
     * 统计数据
     * @return
     */
    @GetMapping("/count")
    public Result<?> count() {
        return Result.success(userMapper.countAddress());
    }
}
