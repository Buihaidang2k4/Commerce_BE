package com.example.MyShop_API.service.report.styleExcel;

import org.apache.poi.ss.usermodel.*;

public class BorderStyleUtil {

    // Hàm static tạo style border đậm cho header
    public static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        // Border đậm
        cellStyle.setBorderTop(BorderStyle.THICK);
        cellStyle.setBorderBottom(BorderStyle.THICK);
        cellStyle.setBorderRight(BorderStyle.THICK);
        cellStyle.setBorderLeft(BorderStyle.THICK);

        // Căn giữa
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Font in đậm
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }

    // Hàm static tạo style border đậm cho dữ liệu
    public static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setBorderTop(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THICK);
        style.setBorderRight(BorderStyle.THICK);

        // Căn trái cho dữ liệu
        style.setAlignment(HorizontalAlignment.LEFT);

        return style;
    }
}
