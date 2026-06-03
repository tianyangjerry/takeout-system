package com.njit.takeoutsystem.mapper;

import com.njit.takeoutsystem.entity.Cart;
import com.njit.takeoutsystem.entity.CartItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CartMapper {

    @Select("SELECT id FROM cart WHERE user_id = #{userId} LIMIT 1")
    Long findCartIdByUserId(Long userId);

    @Insert("INSERT INTO cart (user_id) VALUES (#{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCart(Cart cart);

    @Select("""
            SELECT ci.*, d.name AS dish_name, d.price, d.stock, d.status, d.image_url
            FROM cart_item ci
            JOIN cart c ON c.id = ci.cart_id
            JOIN dish d ON d.id = ci.dish_id
            WHERE c.user_id = #{userId}
            ORDER BY ci.id DESC
            """)
    List<CartItem> findItemsByUserId(Long userId);

    @Select("""
            SELECT ci.*, d.name AS dish_name, d.price, d.stock, d.status, d.image_url
            FROM cart_item ci
            JOIN dish d ON d.id = ci.dish_id
            WHERE ci.cart_id = #{cartId} AND ci.dish_id = #{dishId}
            LIMIT 1
            """)
    CartItem findItemByCartAndDish(@Param("cartId") Long cartId, @Param("dishId") Long dishId);

    @Select("""
            SELECT ci.*, d.name AS dish_name, d.price, d.stock, d.status, d.image_url
            FROM cart_item ci
            JOIN cart c ON c.id = ci.cart_id
            JOIN dish d ON d.id = ci.dish_id
            WHERE c.user_id = #{userId} AND ci.id = #{itemId}
            LIMIT 1
            """)
    CartItem findUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Insert("""
            INSERT INTO cart_item (cart_id, dish_id, quantity)
            VALUES (#{cartId}, #{dishId}, #{quantity})
            """)
    int insertItem(@Param("cartId") Long cartId, @Param("dishId") Long dishId, @Param("quantity") Integer quantity);

    @Update("UPDATE cart_item SET quantity = #{quantity} WHERE id = #{id}")
    int updateItemQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Delete("""
            DELETE ci FROM cart_item ci
            JOIN cart c ON c.id = ci.cart_id
            WHERE c.user_id = #{userId} AND ci.id = #{itemId}
            """)
    int deleteUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Delete("""
            DELETE ci FROM cart_item ci
            JOIN cart c ON c.id = ci.cart_id
            WHERE c.user_id = #{userId}
            """)
    int clearByUserId(Long userId);
}
