package com.njit.takeoutsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    @NotBlank
    private String status;
}
