package com.zzt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzt.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
