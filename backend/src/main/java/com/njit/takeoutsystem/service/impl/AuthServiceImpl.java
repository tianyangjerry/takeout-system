package com.njit.takeoutsystem.service.impl;

import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.dto.LoginRequest;
import com.njit.takeoutsystem.dto.RegisterRequest;
import com.njit.takeoutsystem.dto.UpdateUserRequest;
import com.njit.takeoutsystem.entity.User;
import com.njit.takeoutsystem.mapper.UserMapper;
import com.njit.takeoutsystem.service.AuthService;
import com.njit.takeoutsystem.utils.JwtUtil;
import com.njit.takeoutsystem.utils.PasswordUtil;
import com.njit.takeoutsystem.vo.LoginVO;
import com.njit.takeoutsystem.vo.UserVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {
    private static final String ADMIN_REGISTER_CODE = "yangyang";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CUSTOMER = "CUSTOMER";

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UserVO register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException(409, "用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.hash(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(resolveRegisterRole(request));
        userMapper.insert(user);
        return UserVO.from(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        return new LoginVO(jwtUtil.generateToken(user), UserVO.from(user));
    }

    @Override
    public UserVO getCurrentUser(String authorizationHeader) {
        return UserVO.from(loadCurrentUser(authorizationHeader));
    }

    @Override
    @Transactional
    public void updateCurrentUser(String authorizationHeader, UpdateUserRequest request) {
        User user = loadCurrentUser(authorizationHeader);
        String phone = StringUtils.hasText(request.getPhone()) ? request.getPhone() : user.getPhone();
        String address = StringUtils.hasText(request.getAddress()) ? request.getAddress() : user.getAddress();
        String avatar = StringUtils.hasText(request.getAvatar()) ? request.getAvatar() : user.getAvatar();
        userMapper.updateProfile(user.getId(), phone, address, avatar);
    }

    private User loadCurrentUser(String authorizationHeader) {
        Long userId = jwtUtil.parseUserId(jwtUtil.resolveToken(authorizationHeader));
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(401, "登录用户不存在");
        }
        return user;
    }

    private String resolveRegisterRole(RegisterRequest request) {
        String role = StringUtils.hasText(request.getRole()) ? request.getRole().trim().toUpperCase() : ROLE_CUSTOMER;
        if (ROLE_CUSTOMER.equals(role)) {
            return ROLE_CUSTOMER;
        }
        if (ROLE_ADMIN.equals(role)) {
            if (!ADMIN_REGISTER_CODE.equals(request.getAdminCode())) {
                throw new BusinessException(400, "管理员注册码错误");
            }
            return ROLE_ADMIN;
        }
        throw new BusinessException(400, "注册角色无效");
    }
}
