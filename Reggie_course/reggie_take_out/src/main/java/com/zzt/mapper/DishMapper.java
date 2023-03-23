package com.zzt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzt.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
