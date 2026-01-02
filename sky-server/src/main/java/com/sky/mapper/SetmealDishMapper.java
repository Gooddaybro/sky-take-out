package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //传入这个方法
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
}
