package com.zzt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzt.common.exception.CustomException;
import com.zzt.domain.Setmeal;
import com.zzt.domain.SetmealDish;
import com.zzt.domain.dto.SetmealDto;
import com.zzt.mapper.SetmealMapper;
import com.zzt.service.SetmealDishService;
import com.zzt.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，1、保存套餐基本信息；2、保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal表,这里直接setmealDto向上转型
        this.save(setmealDto);
        //保存套餐和菜品的关联信息，操作setmeal_dish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    public void deleteWithDish(List<Long> ids) {
        //若当前要删除的套餐ids中有一个是在售的，就不删除
        LambdaQueryWrapper<Setmeal> setmealWrapper =new LambdaQueryWrapper<>();
        //select count(*) from setmeal_dish where id in (xxx) and status == 1;
        setmealWrapper.eq(Setmeal::getStatus,1);
        setmealWrapper.in(ids.size()>0,Setmeal::getId,ids);
        int count = this.count(setmealWrapper);
        if(count>0){
            throw new CustomException("不能删除非停售套餐");
        }
        //如果可以删除，先删除套餐表setmeal中的数据
        /*setmealWrapper.clear();
        setmealWrapper.in(ids.size()>0,Setmeal::getId,ids);
        this.remove(setmealWrapper);*/
        this.removeByIds(ids);
        //再删除关系表setmeal_dish中的数据
        //delete from setmeal_dish where setmeal_id in ()
        LambdaQueryWrapper<SetmealDish> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(wrapper);
    }

}
