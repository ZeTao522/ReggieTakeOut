package com.zzt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.domain.ShoppingCart;
import com.zzt.mapper.ShoppingCartMapper;
import com.zzt.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
