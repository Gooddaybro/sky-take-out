package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 营业额统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dates) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : dates) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);

            map.put("endTime", endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dates, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dates.add(begin);
        }
        List<Integer> totalOrderList = new ArrayList<>();
        List<Integer> newOrderList = new ArrayList<>();
        for (LocalDate date : dates) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer totalOrderCount=getOrderCount(beginTime,endTime,null);
            Integer validOrderCount=getOrderCount(beginTime,endTime,Orders.COMPLETED);
            totalOrderList.add(totalOrderCount);
            newOrderList.add(validOrderCount);
        }
        Integer totalOrderCount=totalOrderList.stream().mapToInt(Integer::intValue).sum();
        Integer validOrderCount=newOrderList.stream().mapToInt(Integer::intValue).sum();
        Double completionRate=0.0;
        if(totalOrderCount!=0){
            completionRate=validOrderCount.doubleValue()/totalOrderCount.doubleValue();
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dates,","))
                .orderCountList(StringUtils.join(totalOrderList,","))
                .validOrderCountList(StringUtils.join(newOrderList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(completionRate)
                .build();
    }
    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end,Integer status) {
        Map map = new HashMap();
        map.put("beginTime", begin);
        map.put("endTime", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }

    /**
     * 销量排名
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(begin, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10=orderMapper.getSalesTop10(beginTime,endTime);
        List<String> names=salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList=StringUtils.join(names, ",");
        List<Integer>numbers=salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList=StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 营业额导出
     * @param begin
     * @param end
     * @param response
     * @throws IOException
     */
    @Override
    public void exportTurnover(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException {
        TurnoverReportVO vo = getTurnoverStatistics(begin, end);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("营业额统计");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("日期");
        header.createCell(1).setCellValue("营业额(元)");

        String[] dates = StringUtils.split(vo.getDateList(), ",");
        String[] turnovers = StringUtils.split(vo.getTurnoverList(), ",");
        for (int i = 0; i < dates.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dates[i]);
            row.createCell(1).setCellValue(Double.parseDouble(turnovers[i]));
        }

        writeWorkbookToResponse(workbook, "turnover", begin, end, response);
    }

    /**
     * 订单导出
     * @param begin
     * @param end
     * @param response
     * @throws IOException
     */
    @Override
    public void exportOrders(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException {
        OrderReportVO vo = getOrderStatistics(begin, end);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("订单统计");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("日期");
        header.createCell(1).setCellValue("订单总数");
        header.createCell(2).setCellValue("有效订单数");
        header.createCell(3).setCellValue("完成率");

        String[] dates = StringUtils.split(vo.getDateList(), ",");
        String[] totalOrders = StringUtils.split(vo.getOrderCountList(), ",");
        String[] validOrders = StringUtils.split(vo.getValidOrderCountList(), ",");
        for (int i = 0; i < dates.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dates[i]);
            row.createCell(1).setCellValue(Integer.parseInt(totalOrders[i]));
            row.createCell(2).setCellValue(Integer.parseInt(validOrders[i]));

            int total = Integer.parseInt(totalOrders[i]);
            int valid = Integer.parseInt(validOrders[i]);
            double rate = total == 0 ? 0.0 : (double) valid / total;
            row.createCell(3).setCellValue(rate);
        }

        int summaryRowIndex = dates.length + 2;
        Row summary = sheet.createRow(summaryRowIndex);
        summary.createCell(0).setCellValue("汇总");
        summary.createCell(1).setCellValue(vo.getTotalOrderCount());
        summary.createCell(2).setCellValue(vo.getValidOrderCount());
        summary.createCell(3).setCellValue(vo.getOrderCompletionRate());

        writeWorkbookToResponse(workbook, "orders", begin, end, response);
    }

    /**
     * 销量前10导出
     * @param begin
     * @param end
     * @param response
     * @throws IOException
     */
    @Override
    public void exportSalesTop10(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException {
        SalesTop10ReportVO vo = getSalesTop10(begin, end);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("销量Top10");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("商品名称");
        header.createCell(1).setCellValue("销量");

        String[] names = StringUtils.split(vo.getNameList(), ",");
        String[] numbers = StringUtils.split(vo.getNumberList(), ",");
        if (names != null && numbers != null) {
            for (int i = 0; i < names.length; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(names[i]);
                row.createCell(1).setCellValue(Integer.parseInt(numbers[i]));
            }
        }

        writeWorkbookToResponse(workbook, "sales-top10", begin, end, response);
    }

    /**
     * 综合导出全部
     * @param begin
     * @param end
     * @param response
     * @throws IOException
     */
    @Override
    public void exportBusiness(LocalDate begin, LocalDate end, HttpServletResponse response) throws IOException {
        TurnoverReportVO turnoverVO = getTurnoverStatistics(begin, end);
        OrderReportVO orderVO = getOrderStatistics(begin, end);
        UserReportVO userVO = getUserStatistics(begin, end);

        double totalTurnover = 0.0;
        String[] turnovers = StringUtils.split(turnoverVO.getTurnoverList(), ",");
        if (turnovers != null) {
            for (String t : turnovers) {
                if (StringUtils.isNotEmpty(t)) {
                    totalTurnover += Double.parseDouble(t);
                }
            }
        }

        Integer validOrderCount = orderVO.getValidOrderCount() == null ? 0 : orderVO.getValidOrderCount();
        Double orderCompletionRate = orderVO.getOrderCompletionRate() == null ? 0.0 : orderVO.getOrderCompletionRate();

        int newUsers = 0;
        String[] newUserArr = StringUtils.split(userVO.getNewUserList(), ",");
        if (newUserArr != null) {
            for (String n : newUserArr) {
                if (StringUtils.isNotEmpty(n)) {
                    newUsers += Integer.parseInt(n);
                }
            }
        }
        double unitPrice = (validOrderCount == 0) ? 0.0 : totalTurnover / validOrderCount;
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("综合运营报表");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("开始日期");
        header.createCell(1).setCellValue("结束日期");
        header.createCell(2).setCellValue("总营业额(元)");
        header.createCell(3).setCellValue("有效订单数");
        header.createCell(4).setCellValue("订单完成率");
        header.createCell(5).setCellValue("平均客单价(元)");
        header.createCell(6).setCellValue("新增用户数");
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(begin.toString());
        row.createCell(1).setCellValue(end.toString());
        row.createCell(2).setCellValue(totalTurnover);
        row.createCell(3).setCellValue(validOrderCount);
        row.createCell(4).setCellValue(orderCompletionRate);
        row.createCell(5).setCellValue(unitPrice);
        row.createCell(6).setCellValue(newUsers);

        writeWorkbookToResponse(workbook, "business", begin, end, response);
    }

    private void writeWorkbookToResponse(Workbook workbook,
                                         String prefix,
                                         LocalDate begin,
                                         LocalDate end,
                                         HttpServletResponse response) throws IOException {
        String fileName = String.format("%s-%s_%s.xlsx", prefix, begin.toString(), end.toString());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
