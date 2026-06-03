package com.njit.takeoutsystem.vo;

import com.njit.takeoutsystem.entity.CartItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;
    private Long dishId;
    private String dishName;
    private BigDecimal price;
    private Integer quantity;
    private Integer stock;
    private String imageUrl;
    private BigDecimal subtotal;

    public static CartItemVO from(CartItem item) {
        CartItemVO vo = new CartItemVO();
        vo.setId(item.getId());
        vo.setDishId(item.getDishId());
        vo.setDishName(item.getDishName());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        vo.setStock(item.getStock());
        vo.setImageUrl(item.getImageUrl());
        vo.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return vo;
    }
}
