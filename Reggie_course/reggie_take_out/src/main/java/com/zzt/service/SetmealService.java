package com.zzt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzt.domain.Setmeal;
import com.zzt.domain.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，1、保存套餐基本信息；2、保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，1、判断是否停售；2、1成立则删除套餐基本信息；3、删除套餐和菜品的关联关系
     * @param ids
     */
    void deleteWithDish(List<Long> ids);
}
