package com.njit.takeoutsystem.vo;

import lombok.Data;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private Integer sortOrder;
    private Integer dishCount;
}
