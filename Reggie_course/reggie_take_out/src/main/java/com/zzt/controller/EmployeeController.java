package com.zzt.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzt.common.R;
import com.zzt.domain.Employee;
import com.zzt.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")/*request 用于获取session*/
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("员工尝试登录");
        //1,将页面提交的密码password进行md5加密(数据库的password也md5加密了)
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2,根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//数据库已对username做唯一约束

        //3,如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败,请检查用户名或密码");
        }

        //4,密码对比,如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败,请检查用户名或密码");
        }

        //5,查看员工状态,如果为已禁用状态(status==0),则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6,登录成功,将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        log.info("员工退出登录");
        //清除Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出登录成功");
    }

    /**
     * 添加员工
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息:{}", employee);

        //统一初始密码，md5加密123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //手动设置创建时间与更新时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //手动设置创建人与更新人信息
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        log.info("新增员工成功!");

        return R.success("新增员工成功!");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("分页查询 page = {}, pageSize = {}, name = {}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        //name非空时才添加进过滤条件
        queryWrapper.like(name != null, Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id更新员工信息
     *
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        //禁止管理员自己封自己
        Employee who = employeeService.getById(employee.getId());
        if (who != null && who.getUsername().equals("admin") && employee.getStatus() == 0) {
            return R.error("管理员账号不可禁用");
        }

        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        //MP的update对于null的属性会保留原来的值，因此无需考虑
        employeeService.updateById(employee);

        return R.success("修改成功");
    }

    /**
     * 根据id查找员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> get(@PathVariable long id) {
        Employee employee = employeeService.getById(id);

        if (employee != null)
            return R.success(employee);
        else
            return R.error("没有查询到此员工相关信息");

    }
}
