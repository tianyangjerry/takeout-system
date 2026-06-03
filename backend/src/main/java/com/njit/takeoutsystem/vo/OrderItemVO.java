package com.njit.takeoutsystem.vo;

import com.njit.takeoutsystem.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Long dishId;
    private String dishName;
    private BigDecimal dishPrice;
    private Integer quantity;
    private BigDecimal subtotal;

    public static OrderItemVO from(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setDishId(item.getDishId());
        vo.setDishName(item.getDishName());
        vo.setDishPrice(item.getDishPrice());
        vo.setQuantity(item.getQuantity());
        vo.setSubtotal(item.getSubtotal());
        return vo;
    }
}
