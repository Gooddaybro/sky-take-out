package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量排名
     * @param begin
     * @param end
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
     * 营业额报表导出
     */
    void exportTurnover(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException;

    /**
     * 订单报表导出
     */
    void exportOrders(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException;

    /**
     * 销量Top10报表导出
     */
    void exportSalesTop10(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException;

    /**
     * 综合运营报表导出
     */
    void exportBusiness(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException;
}
