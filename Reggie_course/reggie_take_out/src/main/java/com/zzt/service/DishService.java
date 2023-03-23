package com.zzt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzt.domain.Dish;
import com.zzt.domain.dto.DishDto;

public interface DishService extends IService<Dish> {
    /**
     * 分别保存菜品信息和菜品口味信息
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id获取菜品基本信息和口味信息（查两表）
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(long id);

    /**
     * 更新菜品信息和口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 逻辑删除dish表和dish_flavor表数据
     * @param ids
     */
    void deleteByIdsWithFlavor(long[] ids);
}
