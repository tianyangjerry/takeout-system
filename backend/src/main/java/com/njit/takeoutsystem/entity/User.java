package com.njit.takeoutsystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String phone;
    private String role;
    private String address;
    private String avatar;
    private LocalDateTime createdTime;
}
