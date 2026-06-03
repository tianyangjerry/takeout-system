package com.njit.takeoutsystem.service;

import com.njit.takeoutsystem.dto.LoginRequest;
import com.njit.takeoutsystem.dto.RegisterRequest;
import com.njit.takeoutsystem.dto.UpdateUserRequest;
import com.njit.takeoutsystem.vo.LoginVO;
import com.njit.takeoutsystem.vo.UserVO;

public interface AuthService {
    UserVO register(RegisterRequest request);

    LoginVO login(LoginRequest request);

    UserVO getCurrentUser(String authorizationHeader);

    void updateCurrentUser(String authorizationHeader, UpdateUserRequest request);
}
