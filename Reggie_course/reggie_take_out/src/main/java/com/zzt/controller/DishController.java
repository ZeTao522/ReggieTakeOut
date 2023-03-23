package com.zzt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzt.common.R;
import com.zzt.domain.Category;
import com.zzt.domain.Dish;
import com.zzt.domain.DishFlavor;
import com.zzt.domain.dto.DishDto;
import com.zzt.service.CategoryService;
import com.zzt.service.DishFlavorService;
import com.zzt.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) { //这里用到DTO来接收复合数据类型
        log.info("新增菜品 {}", dishDto);
        //调用自定义方法添加dishDto中的菜品和菜品口味至两张表
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @Autowired
    CategoryService categoryService;

    /**
     * 菜品信息分页查询，包括菜品分类名称
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        log.info("菜品分页查询，page={}，pageSize={}，name={}", page, pageSize, name);
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(name != null, Dish::getName, name);
        wrapper.eq(Dish::getIsDeleted,0);//只查询未被逻辑删除的
        wrapper.orderByDesc(Dish::getSort);
        dishService.page(pageInfo, wrapper);

        //接下来,准备拷贝
        Page<DishDto> dishDtoPageInfo = new Page<>(page, pageSize);
        List<Dish> records = pageInfo.getRecords();//records是分页查询到的数据集合
        //拷贝除了数据集合以外的分页构造器信息至dishDto分页构造器
        BeanUtils.copyProperties(pageInfo, dishDtoPageInfo, "records");
        //遍历records每一条数据，拷贝至新DishDto集合中，并且查询添加菜品名
        List<DishDto> list = new ArrayList<>();
        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            //拷贝菜品基本信息
            BeanUtils.copyProperties(record, dishDto);
            //根据菜品分类id查询菜品分类并赋值给dishDto.categoryName
            Long categoryId = record.getCategoryId();
            //如果getById(categoryId)为null会报空指针错误，但只要数据库数据没问题就不会出现。
            String CategoryName = categoryService.getById(categoryId).getName();
            dishDto.setCategoryName(CategoryName);
            list.add(dishDto);
        }
        //将新集合添加到新分页构造器中
        dishDtoPageInfo.setRecords(list);
        return R.success(dishDtoPageInfo);
    }

    /**
     * 根据id获取菜品及口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable long id) {
        log.info("菜品单个详情查询,id={}", id);
        DishDto withFlavor = dishService.getByIdWithFlavor(id);

        return R.success(withFlavor);
    }

    /**
     * 修改菜品信息和口味信息
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("修改菜品，id={}", dishDto.getId());
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }


    /**
     * 单个/批量停售/起售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> stop(@PathVariable Integer status, long[] ids) {
        log.info("修改菜品售货状态,status={}", status);

        Dish dish = new Dish();
        dish.setStatus(status);//0禁售,1起售
        for (long id : ids) {
            dish.setId(id);
            dishService.updateById(dish);
        }

        String message;
        if (ids.length > 1)
            message = "批量修改售货状态成功";
        else
            message = "修改售货状态成功";
        return R.success(message);
    }

    /**
     * 单个/批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(long[] ids) {
        //注意,此删除不是真删除,是逻辑删除,即设置is_deleted字段为1
        log.info("逻辑删除菜品,删除个数:{}", ids.length);

        dishService.deleteByIdsWithFlavor(ids);

        String message;
        if (ids.length > 1)
            message = "批量删除成功";
        else
            message = "删除成功";
        return R.success(message);
    }

    @Autowired
    DishFlavorService dishFlavorService;

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){//dish当参数更灵活通用,将来要接其它的也可以.目前接categoryId或者name
        log.info("查询某类所有菜品,dish={}",dish);

        //条件:菜品分类符合,菜品名称like,菜品在售,菜品未删除
        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        wrapper.like(dish.getName()!=null,Dish::getName,dish.getName());
        wrapper.eq(Dish::getStatus,1);
        wrapper.eq(Dish::getIsDeleted,0);
        //排序
        wrapper.orderByAsc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(wrapper);

        //准备拷贝(为了适应购物车功能)
        List<DishDto> dtoList=new ArrayList<>();
        DishDto dishDto;
        LambdaQueryWrapper<DishFlavor> flavorWrapper=new LambdaQueryWrapper<>();
        for (Dish aDish : dishList) {
            dishDto=new DishDto();
            BeanUtils.copyProperties(aDish,dishDto);
            flavorWrapper.clear();
            flavorWrapper.eq(DishFlavor::getDishId,aDish.getId());
            List<DishFlavor> flavorList = dishFlavorService.list(flavorWrapper);
            dishDto.setFlavors(flavorList);
            dtoList.add(dishDto);
        }

        return R.success(dtoList);
    }

}
