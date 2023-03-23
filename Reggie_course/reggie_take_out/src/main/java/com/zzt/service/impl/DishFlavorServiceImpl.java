package com.zzt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.domain.DishFlavor;
import com.zzt.mapper.DishFlavorMapper;
import com.zzt.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
