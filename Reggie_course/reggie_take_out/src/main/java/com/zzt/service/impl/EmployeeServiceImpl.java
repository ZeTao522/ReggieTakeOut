package com.zzt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.domain.Employee;
import com.zzt.mapper.EmployeeMapper;
import com.zzt.service.EmployeeService;
import org.springframework.stereotype.Service;

/*继承MP提供的ServiceImpl，实现EmployeeService接口（其继承了MP提供的IService）*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
