package com.zzt.common;

import com.zzt.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*
 * 全局异常处理器
 * */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody//返回对象变JSON,不加会bug
@Slf4j
public class GlobalExceptionHandler {

    //捕获数据库新增数据违反唯一原则时异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> sqlExceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        //某值重复
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    //捕获自定义业务异常
    @ExceptionHandler(CustomException.class)
    public R<String> customExceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

    //未知异常
    @ExceptionHandler(Exception.class)
    public R<String> doOtherException(Exception ex){
        //记录日志
        //发送消息给运维
        //发送邮件给开发人员,ex对象发送给开发人员
        return R.error("服务器繁忙");
    }

}
