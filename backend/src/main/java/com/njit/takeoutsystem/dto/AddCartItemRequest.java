package com.njit.takeoutsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemRequest {
    @NotNull
    private Long dishId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
