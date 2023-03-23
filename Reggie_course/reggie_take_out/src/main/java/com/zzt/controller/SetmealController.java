package com.zzt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzt.common.R;
import com.zzt.domain.Category;
import com.zzt.domain.Setmeal;
import com.zzt.domain.dto.SetmealDto;
import com.zzt.service.CategoryService;
import com.zzt.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("新增套餐{}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);
        //查询获得以Setmeal为数据集合的setmealPage
        setmealService.page(setmealPage, wrapper);

        //接下来准备拷贝，为了让前端展示套餐分类
        //构造新分页构造器
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        //查找套餐分类名，对records集合数据处理后拷贝至新分页构造器的records
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> newRecords = new ArrayList<>();
        for (Setmeal record : records) {
            //拷贝相同信息
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);
            //查套餐名
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            //加入集合
            newRecords.add(setmealDto);
        }
        setmealDtoPage.setRecords(newRecords);

        //现在返回的数据类型就和前端需要的匹配上了，能显示分类名了。
        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除套餐，ids={}", ids);

        setmealService.deleteWithDish(ids);

        String message;
        if (ids.size() > 1) message = "批量删除套餐成功";
        else message = "删除套餐成功";
        return R.success(message);
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("查询某类所有套餐,setmeal={}",setmeal);

        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        wrapper.eq(Setmeal::getStatus,1);
        wrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(wrapper);

        return R.success(list);
    }

}
