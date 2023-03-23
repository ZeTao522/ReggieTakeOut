package com.zzt.domain.dto;

import com.zzt.domain.OrderDetail;
import com.zzt.domain.Orders;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private List<OrderDetail> orderDetails=new ArrayList<>();

}
