package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     *检查当前要删除的菜品是否被关联到了某个套餐中
     * @param dishIds
     * @return
     */
    List<Long> getsetmeal_dish(List<Long> dishIds);

    /**
     * 菜品套餐关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 批量删除菜品与套餐之间的关系
     * @param ids
     */
    void deleteByids(List<Long> ids);

    /**
     * 根据id查询关联套餐
     * @param id
     * @return
     */
    List<SetmealDish> getBysetmealId(Long id);

    /**
     * 删除菜品套餐关系
     * @param id
     */
    void deleteBySetmealId(Long id);
}
