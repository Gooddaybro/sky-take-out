package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 定时每分钟处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void task() {
        LocalDateTime time=LocalDateTime.now().minusMinutes(15);
        log.info("开始处理超时订单,判定时间点{}",time);
        List<Orders> ordersList=orderMapper.getLateOrder(time);
        if(ordersList.size()>0&&ordersList.isEmpty()) {
            for(Orders orders:ordersList) {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时未支付，自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
                log.info("订单ID:{}已经超时取消",orders.getId());
            }
        }
    }
}
