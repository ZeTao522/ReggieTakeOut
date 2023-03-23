package com.zzt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzt.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper/*注意mapper是代理接口，别写成类了*/
public interface EmployeeMapper extends BaseMapper<Employee> {
}
