package com.njit.takeoutsystem.vo;

import com.njit.takeoutsystem.entity.Dish;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DishVO {
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
    private BigDecimal recommendScore;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public static DishVO from(Dish dish) {
        DishVO vo = new DishVO();
        vo.setId(dish.getId());
        vo.setCategoryId(dish.getCategoryId());
        vo.setCategoryName(dish.getCategoryName());
        vo.setName(dish.getName());
        vo.setPrice(dish.getPrice());
        vo.setStock(dish.getStock());
        vo.setImageUrl(dish.getImageUrl());
        vo.setDescription(dish.getDescription());
        vo.setStatus(dish.getStatus());
        vo.setSales(dish.getSales());
        vo.setRating(dish.getRating());
        vo.setCreatedTime(dish.getCreatedTime());
        vo.setUpdatedTime(dish.getUpdatedTime());
        return vo;
    }
}
