package com.zzt.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzt.domain.Category;

public interface CategoryService extends IService<Category> {
    void remove(long id);
}
