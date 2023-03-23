package com.zzt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.domain.OrderDetail;
import com.zzt.mapper.OrderDetailMapper;
import com.zzt.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}