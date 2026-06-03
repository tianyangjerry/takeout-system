package com.njit.takeoutsystem.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String phone;
    private String address;
    private String avatar;
}
