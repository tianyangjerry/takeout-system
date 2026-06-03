package com.njit.takeoutsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String name;

    private Integer sortOrder = 0;
}
