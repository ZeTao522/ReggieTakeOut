package com.zzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.common.exception.CustomException;
import com.zzt.domain.Category;
import com.zzt.domain.Dish;
import com.zzt.domain.Setmeal;
import com.zzt.mapper.CategoryMapper;
import com.zzt.service.CategoryService;
import com.zzt.service.DishService;
import com.zzt.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(long id) {
        //不判断这个分类是什么分类的话,那就对两种都查一次.
        //构造条件构造器
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();

        //1,判断当前分类是否关联了菜品
        dishWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishWrapper);
        if (count1 > 0) {
            //已经关联了菜品,抛出一个自定义异常类(由于是继承RuntimeException,因此无需声明)
            throw new CustomException("该分类已关联菜品,无法删除");
        }

        //2,判断当前分类是否关联了套餐
        setmealWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealWrapper);
        if (count2 > 0) {
            //已经关联了套餐,抛出一个自定义异常类(由于是继承RuntimeException,因此无需声明)
            throw new CustomException("该分类已关联套餐,无法删除");
        }

        //3,走到这里说明此分类未被关联,可以删除,super调用MP提供的方法
        super.removeById(id);
    }
}
