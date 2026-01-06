package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 用户下单
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付（下单或模拟支付）
     * @param ordersPaymentDTO
     * @return 支付调用参数
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    /**
     * 支付成功回调/模拟确认
     * @param orderNumber
     * @param payMethod
     */
    void paySuccess(String orderNumber,Integer payMethod);
}
