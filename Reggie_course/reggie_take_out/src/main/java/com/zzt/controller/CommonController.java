package com.zzt.controller;

import com.zzt.common.R;
import com.zzt.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传，保存至basePath
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) { //注意MultipartFilter参数的名称要与提交的formdata中的name属性值相同
        log.info("上传文件 {}", file.toString());

        //提取原文件格式后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //UUID生成32位随机字符串,加上后缀构成保存到服务端的新文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        /*//判断指定位置目录是否存在
        File dir = new File(basePath);
        if(!dir.exists()){
            //如果目录不存在，则需要创建（一次性）
            dir.mkdirs();
        }*/

        //将临时文件存储到指定位置
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            //throw new RuntimeException(e);
            throw new CustomException(e.getMessage());
        }

        return R.success(fileName);
    }

    /**
     * 文件下载,通过输出流输出到浏览器,而不通过返回值
     *
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        log.info("图片下载 {}", name);
        try {
            //输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            //输出流,通过输出流将文件写回浏览器,在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");//设置种类
            //传统读取操作
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();//刷新
            }
            //关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }
}
