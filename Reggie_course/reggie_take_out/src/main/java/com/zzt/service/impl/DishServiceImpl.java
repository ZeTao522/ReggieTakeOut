package com.zzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.domain.Dish;
import com.zzt.domain.DishFlavor;
import com.zzt.domain.dto.DishDto;
import com.zzt.mapper.DishMapper;
import com.zzt.service.DishFlavorService;
import com.zzt.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 分别保存菜品信息和菜品口味信息
     * @param dishDto
     */
    @Override
    @Transactional//涉及多张表增删改,开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品信息至菜品表dish,这里是向上转型.
        this.save(dishDto);

        Long id = dishDto.getId();
        //菜品口味信息关联菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(id);
            //flavor.setIsDeleted(0);SQL有默认值
            //dishFlavorService.save(flavor);
        }
        //批量保存菜品口味信息到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id获取菜品基本信息和口味信息（查两表）
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(long id) {
        Dish justDish = this.getById(id);
        //构造条件构造器
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);//注意这里不要get错id
        List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
        //两部分用来初始化dishDto
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(justDish, dishDto);
        dishDto.setFlavors(dishFlavorList);

        return dishDto;
    }

    /**
     * 更新菜品信息和口味信息
     * @param dishDto
     */
    @Override
    @Transactional//涉及多张表增删改,开启事务
    public void updateWithFlavor(DishDto dishDto) {
        //将dish_flavor表的当前菜品相关口味删除
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        Long dishId = dishDto.getId();
        wrapper.eq(dishId != null, DishFlavor::getDishId, dishId);
        dishFlavorService.remove(wrapper);
        //再将当前dishDto信息更新至dish表和dish_flavor表，注意是更新，不能this.saveWithFlavor(dishDto)
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            //关联菜品id信息
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
        Dish dish = dishDto;
        this.updateById(dish);
    }


    /**
     * 逻辑删除dish表和dish_flavor表数据
     * @param ids
     */
    @Override
    public void deleteByIdsWithFlavor(long[] ids) {
        Dish dish = new Dish();
        dish.setIsDeleted(1);
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        DishFlavor dishFlavor = new DishFlavor();
        dishFlavor.setIsDeleted(1);
        for (long id : ids) {
            //逻辑删除dish表数据
            dish.setId(id);
            this.updateById(dish);
            //逻辑删除dish_flavor表数据
            wrapper.eq(DishFlavor::getDishId, id);
            dishFlavorService.update(dishFlavor, wrapper);
        }
    }
}
