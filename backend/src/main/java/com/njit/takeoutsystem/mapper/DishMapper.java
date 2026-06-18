package com.njit.takeoutsystem.mapper;

import com.njit.takeoutsystem.dto.DishQuery;
import com.njit.takeoutsystem.entity.Dish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    @Select("""
            <script>
            SELECT d.*, c.name AS category_name
            FROM dish d
            LEFT JOIN category c ON c.id = d.category_id
            <where>
                <if test="keyword != null and keyword != ''">
                    AND d.name LIKE CONCAT('%', #{keyword}, '%')
                </if>
                <if test="categoryId != null">
                    AND d.category_id = #{categoryId}
                </if>
                <if test="queryStatus != null">
                    AND d.status = #{queryStatus}
                </if>
            </where>
            ORDER BY
            <choose>
                <when test="sort == 'sales'">d.sales DESC</when>
                <when test="sort == 'price_asc'">d.price ASC</when>
                <when test="sort == 'price_desc'">d.price DESC</when>
                <when test="sort == 'rating'">d.rating DESC</when>
                <otherwise>d.id DESC</otherwise>
            </choose>
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<Dish> findPage(DishQuery query);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM dish d
            <where>
                <if test="keyword != null and keyword != ''">
                    AND d.name LIKE CONCAT('%', #{keyword}, '%')
                </if>
                <if test="categoryId != null">
                    AND d.category_id = #{categoryId}
                </if>
                <if test="queryStatus != null">
                    AND d.status = #{queryStatus}
                </if>
            </where>
            </script>
            """)
    long count(DishQuery query);

    @Select("""
            SELECT d.*, c.name AS category_name
            FROM dish d
            LEFT JOIN category c ON c.id = d.category_id
            WHERE d.id = #{id}
            LIMIT 1
            """)
    Dish findById(Long id);

    @Select("SELECT COUNT(*) FROM order_item WHERE dish_id = #{id}")
    long countOrderItems(Long id);

    @Select("""
            SELECT d.*, c.name AS category_name
            FROM dish d
            LEFT JOIN category c ON c.id = d.category_id
            WHERE d.status = 1
            ORDER BY d.sales DESC, d.id DESC
            LIMIT #{limit}
            """)
    List<Dish> findTop(@Param("limit") int limit);

    @Select("""
            SELECT d.*, c.name AS category_name
            FROM dish d
            LEFT JOIN category c ON c.id = d.category_id
            WHERE d.status = 1
            ORDER BY (d.sales * 0.5 + d.rating * 20) DESC, d.id DESC
            LIMIT #{limit}
            """)
    List<Dish> findRecommend(@Param("limit") int limit);

    @Insert("""
            INSERT INTO dish (category_id, name, price, stock, image_url, description, status)
            VALUES (#{categoryId}, #{name}, #{price}, #{stock}, #{imageUrl}, #{description}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Dish dish);

    @Update("""
            UPDATE dish
            SET category_id = #{categoryId},
                name = #{name},
                price = #{price},
                stock = #{stock},
                image_url = #{imageUrl},
                description = #{description},
                status = #{status}
            WHERE id = #{id}
            """)
    int update(Dish dish);

    @Update("UPDATE dish SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Delete("DELETE FROM dish WHERE id = #{id}")
    int delete(Long id);

    @Update("""
            UPDATE dish
            SET stock = stock - #{quantity}
            WHERE id = #{id} AND stock >= #{quantity}
            """)
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE dish SET stock = stock + #{quantity} WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE dish SET sales = sales + #{quantity} WHERE id = #{id}")
    int increaseSales(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE dish SET sales = GREATEST(sales - #{quantity}, 0) WHERE id = #{id}")
    int decreaseSales(@Param("id") Long id, @Param("quantity") Integer quantity);
}
