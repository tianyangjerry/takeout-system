package com.njit.takeoutsystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusLog {
    private Long id;
    private Long orderId;
    private String status;
    private String statusText;
    private Long operatorId;
    private String operatorRole;
    private String remark;
    private LocalDateTime createdTime;
}
