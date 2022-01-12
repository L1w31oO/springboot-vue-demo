package com.lw.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.lw.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${server.port}")
    private String port;

    private static final String ip = "http://localhost";

    /**
     * 文件上传接口
     * @param file
     * @return
     * @throws IOException
     */
    @CrossOrigin
    @PostMapping("/upload")
    public Result<?> upload(MultipartFile file) throws IOException {
        // 获取源文件的名称
        String originalFilename = file.getOriginalFilename();

        // 定义文件的唯一标识(前缀)
        // 1. hutool的RandomUtil
        // 2. 时间戳System.currentTimeMillis()
        // 3. hutool的IdUtil(UUID)
        String flag = IdUtil.fastSimpleUUID();

        // System.getProperty("user.dir")获取项目(springboot-vue-demo)所在的文件路径
        // 拼接上传路径
        String rootFilePath = System.getProperty("user.dir") + "/src/main/resources/files/" + flag + "_" + originalFilename;

        // 文件写入到上传路径
        FileUtil.writeBytes(file.getBytes(), rootFilePath);

        // 返回结果url
        return Result.success(ip + ":" + port + "/files/" + flag);
    }

    /**
     * 富文本编辑器wangEditor定制的文件上传接口
     * @param file
     * @return
     * @throws IOException
     */
    @CrossOrigin
    @PostMapping("/editor/upload")
    public JSON editorUpload(MultipartFile file) throws IOException {
        // 获取源文件的名称
        String originalFilename = file.getOriginalFilename();

        // 定义文件的唯一标识(前缀)
        // 1. hutool的RandomUtil
        // 2. 时间戳System.currentTimeMillis()
        // 3. hutool的IdUtil(UUID)
        String flag = IdUtil.fastSimpleUUID();

        // System.getProperty("user.dir")获取项目(springboot-vue-demo)所在的文件路径
        // 拼接上传路径
        String rootFilePath = System.getProperty("user.dir") + "/src/main/resources/files/" + flag + "_" + originalFilename;

        // 文件写入到上传路径
        FileUtil.writeBytes(file.getBytes(), rootFilePath);

        // 返回结果url
        String url = ip + ":" + port + "/files/" + flag;

        // 定义富文本编辑器wangEditor返回的json数据格式
        JSONObject json = new JSONObject();
        json.set("errno", 0);

        JSONArray arr = new JSONArray();
        JSONObject data = new JSONObject();
        arr.add(data);
        data.set("url", url);
        json.set("data", arr);
        return json;
    }

    /**
     * 文件下载接口
     * @param flag
     * @param response
     */
    @GetMapping("/{flag}")
    public void getFiles(@PathVariable String flag, HttpServletResponse response) {
        // 新建一个输出流
        OutputStream os;
        // 定义文件上传的根路径
        String basePath = System.getProperty("user.dir") + "/src/main/resources/files/";
        // 获取所有文件名称
        List<String> listFileNames = FileUtil.listFileNames(basePath);
        // 找到和前端传入参数一致的文件
        String fileName = listFileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        try {
            if (StrUtil.isNotEmpty(fileName)) {
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
                response.setContentType("application/octet-stream");
                // 通过文件的路径读取文件字节流
                byte[] bytes = FileUtil.readBytes(basePath + fileName);
                // 通过输出流返回文件
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败！");
        }
    }
}
