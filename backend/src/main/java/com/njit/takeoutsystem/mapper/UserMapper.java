package com.njit.takeoutsystem.mapper;

import com.njit.takeoutsystem.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM `user` WHERE username = #{username} LIMIT 1")
    User findByUsername(String username);

    @Select("SELECT * FROM `user` WHERE id = #{id} LIMIT 1")
    User findById(Long id);

    @Insert("""
            INSERT INTO `user` (username, password, phone, role, address, avatar)
            VALUES (#{username}, #{password}, #{phone}, #{role}, #{address}, #{avatar})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("""
            UPDATE `user`
            SET phone = #{phone}, address = #{address}, avatar = #{avatar}
            WHERE id = #{id}
            """)
    int updateProfile(@Param("id") Long id, @Param("phone") String phone, @Param("address") String address, @Param("avatar") String avatar);
}
