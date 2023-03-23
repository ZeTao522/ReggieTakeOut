package com.zzt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.zzt.common.BaseContext;
import com.zzt.common.R;
import com.zzt.domain.OrderDetail;
import com.zzt.domain.Orders;
import com.zzt.domain.dto.OrdersDto;
import com.zzt.service.OrderDetailService;
import com.zzt.service.OrderService;
import com.zzt.service.impl.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        //提交过来的只有几个信息,其它信息我们根据用户id来查,如购物车信息
        log.info("订单提交:{}", orders);
        orderService.submit(orders);
        return R.success("订单提交成功");
    }


    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 查看历史订单
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> userPage(Integer page, Integer pageSize) {
        Long userId = BaseContext.getCurrentId();
        log.info("查看历史订单,userId={}", userId);
        //构造分页构造器
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, Orders::getUserId, userId);
        orderService.page(ordersPage, wrapper);

        //开始拷贝
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");
        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> newRecords = new ArrayList<>();
        OrdersDto ordersDto;
        LambdaQueryWrapper<OrderDetail> orderDetailWrapper = new LambdaQueryWrapper<>();
        for (Orders record : records) {
            orderDetailWrapper.clear();
            //根据每条Orders信息,查询其多个OrderDetail信息
            orderDetailWrapper.eq(OrderDetail::getOrderId, record.getId());
            List<OrderDetail> list = orderDetailService.list(orderDetailWrapper);
            ordersDto = new OrdersDto();
            ordersDto.setOrderDetails(list);
            BeanUtils.copyProperties(record,ordersDto);
            newRecords.add(ordersDto);
        }

        ordersDtoPage.setRecords(newRecords);

        return R.success(ordersDtoPage);
    }

}
