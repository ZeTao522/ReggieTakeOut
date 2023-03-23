package com.zzt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzt.domain.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}