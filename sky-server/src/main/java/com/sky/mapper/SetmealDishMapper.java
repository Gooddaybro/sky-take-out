package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    //传入这个方法
    List<Long> getsetmeal_dish(List<Long> dishIds);

}
