package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "统计图")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end) {
        log.info("营业额统计数据begin:{},end:{}", begin, end);
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end) {
        log.info("用户统计数据begin:{},end:{}", begin, end);
        UserReportVO userReportVO=reportService.getUserStatistics(begin,end);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("订单统计begin:{},end:{}", begin, end);
        OrderReportVO orderReportVO = reportService.getOrderStatistics(begin, end);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    @ApiOperation("销量排名")
    public Result<SalesTop10ReportVO> salesTop10Statistics(@DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate begin,
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate end){
        log.info("销量排名:{},end:{}", begin, end);
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10(begin, end);
        return Result.success(salesTop10ReportVO);
    }

    /**
     * 营业额报表导出
     */
    @GetMapping("/turnover/export")
    @ApiOperation("营业额报表导出")
    public void exportTurnover(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                               HttpServletResponse response) throws IOException {
        log.info("导出营业额报表，begin:{}, end:{}", begin, end);
        reportService.exportTurnover(begin, end, response);
    }

    /**
     * 订单报表导出
     */
    @GetMapping("/orders/export")
    @ApiOperation("订单报表导出")
    public void exportOrders(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                             HttpServletResponse response) throws IOException {
        log.info("导出订单报表，begin:{}, end:{}", begin, end);
        reportService.exportOrders(begin, end, response);
    }

    /**
     * 销量Top10报表导出
     */
    @GetMapping("/top10/export")
    @ApiOperation("销量Top10报表导出")
    public void exportSalesTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                                 HttpServletResponse response) throws IOException {
        log.info("导出销量Top10报表，begin:{}, end:{}", begin, end);
        reportService.exportSalesTop10(begin, end, response);
    }

    /**
     * 综合运营报表导出（汇总营业额、订单、客单价、新增用户等）
     */
    @GetMapping("/business/export")
    @ApiOperation("综合运营报表导出")
    public void exportBusiness(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                               HttpServletResponse response) throws IOException {
        log.info("导出综合运营报表，begin:{}, end:{}", begin, end);
        reportService.exportBusiness(begin, end, response);
    }

}
