package com.njit.takeoutsystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderTimelineVO {
    private String status;
    private String label;
    private Boolean active;
    private LocalDateTime time;
}
