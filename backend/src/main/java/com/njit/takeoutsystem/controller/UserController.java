package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.dto.UpdateUserRequest;
import com.njit.takeoutsystem.service.AuthService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PutMapping("/me")
    public ApiResponse<Void> updateMe(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody UpdateUserRequest request
    ) {
        authService.updateCurrentUser(authorizationHeader, request);
        return ApiResponse.success("修改成功", null);
    }
}
