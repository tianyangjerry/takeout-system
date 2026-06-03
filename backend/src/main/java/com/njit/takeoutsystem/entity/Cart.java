package com.njit.takeoutsystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
