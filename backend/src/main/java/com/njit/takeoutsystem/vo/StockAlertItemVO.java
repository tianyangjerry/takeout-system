package com.njit.takeoutsystem.vo;

import lombok.Data;

@Data
public class StockAlertItemVO {
    private Long dishId;
    private String dishName;
    private Integer stock;
    private Integer threshold;
    private String status;
    private String statusText;
}
