package com.zzt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzt.common.R;
import com.zzt.domain.Category;
import com.zzt.domain.Employee;
import com.zzt.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("新增分类category: {}", category);
        //当save数据库中已有的分类时,会报重复异常,并被全局异常处理器拦截处理.
        categoryService.save(category);

        String message = category.getType() == 1 ? "添加菜品分类成功" : "添加套餐分类成功";
        return R.success(message);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("分类分页查询 page={},pageSize={}", page, pageSize);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort); //按分类的sort进行升序排序
        //执行分页查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(long id) {
        log.info("删除分类,id={}", id);
        //调用自定义remove方法,当此分类未被关联时才能删除,否则抛业务异常被全局异常处理器捕获.
        categoryService.remove(id);
        return R.success("分类信息删除成功!");
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类,id={}", category.getId());

        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 条件查询(list)
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) { //用category接type
        log.info("条件查询分类,type={}",category.getType());

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort);

        List<Category> list = categoryService.list(wrapper);

        return R.success(list);
    }

}
