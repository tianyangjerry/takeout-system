package com.njit.takeoutsystem.utils;

import com.njit.takeoutsystem.common.BusinessException;
import com.njit.takeoutsystem.entity.User;
import com.njit.takeoutsystem.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserUtil {
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public CurrentUserUtil(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    public User requireLogin(String authorizationHeader) {
        Long userId = jwtUtil.parseUserId(jwtUtil.resolveToken(authorizationHeader));
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException(401, "登录用户不存在");
        }
        return user;
    }

    public User requireAdmin(String authorizationHeader) {
        User user = requireLogin(authorizationHeader);
        if (!"ADMIN".equals(user.getRole())) {
            throw new BusinessException(403, "无权限访问");
        }
        return user;
    }
}
