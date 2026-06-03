package com.njit.takeoutsystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Dish {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private String description;
    private Integer status;
    private Integer sales;
    private BigDecimal rating;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
