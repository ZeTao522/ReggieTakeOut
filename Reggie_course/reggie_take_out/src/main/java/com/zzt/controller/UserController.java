package com.zzt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzt.common.R;
import com.zzt.domain.SimpleMail;
import com.zzt.domain.User;
import com.zzt.service.SendMailService;
import com.zzt.service.UserService;
import com.zzt.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SendMailService sendMailService;

    /**
     * 移动端登录：生成验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        String phone = user.getPhone();
        log.info("移动端获取验证码，phone={}", phone);

        if (phone != null) {
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.warn("code={}", code);
            //发送验证码
            SimpleMail simpleMail=new SimpleMail();
            simpleMail.setFrom("*******");
            simpleMail.setTo(phone);
            simpleMail.setSubject("验证码信息");
            simpleMail.setText(code);
            log.info("mail={}",simpleMail);
            //sendMailService.sendMail(simpleMail);
            log.warn("邮件已发送");
            //将生成的验证码保存到Session(后期优化用redis)
            session.setAttribute(phone, code);

            return R.success("验证码发送成功");
        }
        return R.success("验证码发送失败");
    }

    /**
     * 移动端登录：比对验证码，新用户则自动注册
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("移动端登录，map={}", map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取用户填写的验证码
        String code = map.get("code").toString();
        //从session中获取正确验证码（优化后用redis）
        Object rightCode = session.getAttribute(phone);

        if (rightCode != null && rightCode.equals(code)) {
            //如果比对成功，说明登录成功
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if (user == null) {
                //如果当前用户是新用户，则自动注册入user表
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);//可有可无
                userService.save(user);
                user = userService.getOne(wrapper);//再次获取，以获取id
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginOut(HttpServletRequest request){
        log.info("移动端用户退出登录");
        //清除Session中保存的当前登录用户的id
        request.getSession().removeAttribute("user");
        return R.success("退出登录成功");
    }
}
