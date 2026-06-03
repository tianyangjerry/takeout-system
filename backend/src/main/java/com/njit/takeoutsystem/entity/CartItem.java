package com.njit.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CartItem {
    private Long id;
    private Long cartId;
    private Long dishId;
    private Integer quantity;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    private String dishName;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private String imageUrl;
}
