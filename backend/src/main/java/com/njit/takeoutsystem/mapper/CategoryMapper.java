package com.njit.takeoutsystem.mapper;

import com.njit.takeoutsystem.vo.CategoryVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Select("""
            SELECT c.id, c.name, c.sort_order,
                   COUNT(d.id) AS dish_count
            FROM category c
            LEFT JOIN dish d ON d.category_id = c.id
            GROUP BY c.id, c.name, c.sort_order
            ORDER BY c.sort_order ASC, c.id ASC
            """)
    List<CategoryVO> findAllWithDishCount();

    @Select("SELECT COUNT(*) FROM dish WHERE category_id = #{categoryId}")
    int countDishes(Long categoryId);

    @Select("SELECT COUNT(*) FROM category WHERE id = #{id}")
    int existsById(Long id);

    @Insert("INSERT INTO category (name, sort_order) VALUES (#{name}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CategoryVO category);

    @Update("UPDATE category SET name = #{name}, sort_order = #{sortOrder} WHERE id = #{id}")
    int update(CategoryVO category);

    @Delete("DELETE FROM category WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT id, name, sort_order, 0 AS dish_count FROM category WHERE id = #{id}")
    CategoryVO findById(@Param("id") Long id);
}
