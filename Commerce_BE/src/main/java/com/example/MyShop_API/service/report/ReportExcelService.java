package com.example.MyShop_API.service.report;

import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.service.report.styleExcel.BorderStyleUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReportExcelService {
    @Value("${report.temp-dir-excel}")
    private String tempDir;

    public byte[] exportOrders(List<Order> orders) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");

        // style
        CellStyle headerStyle = BorderStyleUtil.createHeaderStyle(workbook);
        CellStyle dataStyle = BorderStyleUtil.createDataStyle(workbook);


        // create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Mã đơn", "Khách hàng", "Sản phẩm", "Số lượng", "Giá trị"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }

        // write data
        int rowIdx = 1;
        for (Order order : orders) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(order.getOrderId());
            row.createCell(1).setCellValue(order.getDeliveryAddress().getRecipientName());
            row.createCell(2).setCellValue(order.getOrderItems().stream().map(orderItem -> orderItem.getProduct().getProductName()).findFirst().toString());
            row.createCell(3).setCellValue(order.getOrderItems().stream().map(orderItem -> orderItem.getQuantity()).findFirst().toString());
            row.createCell(4).setCellValue(order.getTotalAmount().toString());

            Cell cell = row.createCell(5);
            cell.setCellStyle(dataStyle);
            cell.setCellValue(order.getOrderStatus().toString());

        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // export file
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        //  save file to tempDir
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = tempDir + "/orders_" + timestamp + ".xlsx";

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(out.toByteArray());
        }

        return out.toByteArray();
    }
}
