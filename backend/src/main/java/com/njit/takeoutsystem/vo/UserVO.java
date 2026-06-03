package com.njit.takeoutsystem.vo;

import com.njit.takeoutsystem.entity.User;
import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String phone;
    private String role;
    private String address;
    private String avatar;

    public static UserVO from(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        vo.setAddress(user.getAddress());
        vo.setAvatar(user.getAvatar());
        return vo;
    }
}
