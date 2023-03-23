package com.zzt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzt.common.BaseContext;
import com.zzt.common.R;
import com.zzt.domain.ShoppingCart;
import com.zzt.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加购物车,shoppingCart:{}", shoppingCart);
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //判断添加的是菜品还是套餐
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            //添加的是菜品
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加的是套餐
            wrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }

        //查查看是否添加过
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one != null) {
            //非首次添加,就在原来的数量基础上加一
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            log.info("多次添加，数量+1，numer+1={}", number + 1);
            shoppingCartService.updateById(one);
        } else {
            //首次添加,则save参数传递过来的shoppingCart,手动设置数量和添加时间
            log.info("首次添加");
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long currentId = BaseContext.getCurrentId();
        log.info("查看购物车,userId={}", currentId);

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(currentId != null, ShoppingCart::getUserId, currentId);
        wrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        Long currentId = BaseContext.getCurrentId();
        log.info("清空购物车,userId={}", currentId);

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(currentId != null, ShoppingCart::getUserId, currentId);
        shoppingCartService.remove(wrapper);

        return R.success("清空购物车成功");
    }

    /**
     * 减少购物车中某样菜品/套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long currentId = BaseContext.getCurrentId();
        log.info("减少购物车中某样菜品/套餐,userId={},dishId={}", currentId, shoppingCart.getDishId());

        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        wrapper.eq(ShoppingCart::getUserId,currentId);
        ShoppingCart one = shoppingCartService.getOne(wrapper);

        if(one.getNumber()>1){
            //如果number数量大于1，则-1
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
        }else {
            //如果number数量==1，则删除此购物车数据
            shoppingCartService.removeById(one);
        }

        return R.success("减少购物车中某样菜品/套餐成功");
    }

}
