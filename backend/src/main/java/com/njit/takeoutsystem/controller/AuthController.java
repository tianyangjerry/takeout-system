package com.njit.takeoutsystem.controller;

import com.njit.takeoutsystem.common.ApiResponse;
import com.njit.takeoutsystem.dto.LoginRequest;
import com.njit.takeoutsystem.dto.RegisterRequest;
import com.njit.takeoutsystem.service.AuthService;
import com.njit.takeoutsystem.vo.LoginVO;
import com.njit.takeoutsystem.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("注册成功", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("登录成功", authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return ApiResponse.success(authService.getCurrentUser(authorizationHeader));
    }
}
