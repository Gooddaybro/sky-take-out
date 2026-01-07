package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询
     * @param number
     * @return
     */
    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    /**
     * 更新订单
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单ID查询
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 得到超时订单
     * @param time
     * @return
     */
    List<Orders> getLateOrder(LocalDateTime time);
}
