package com.example.MyShop_API.service.report;

import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.entity.OrderShippingAddress;
import com.example.MyShop_API.entity.OrderItem;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.utils.RadomCodeTemplate;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportPdfService {
    private final com.example.MyShop_API.service.model.ModelLookup models;

    TemplateEngine templateEngine;
    OrderRepository orderRepository;

    @NonFinal
    @Value("${report.temp-dir}")
    private String tempDir;


    // =============== GET REPORT ==================
    public String getReportHtml(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(CommonMessageUtils.ORDER_NOT_EXISTED));
        // mã vận đơn test
        String barCode = RadomCodeTemplate.radomBarcode(15);
        // random mã đơn hàng HC-51-03-GV13
        String randomOrder = RadomCodeTemplate.generateCode();
        // Địa chỉ
        OrderShippingAddress deliveryAddress = models.deliveryAddress(order.getId());
        // Nội dung đơn hàng
        java.util.List<OrderItem> orderItems = models.orderItems(order.getId());
        int sizeOrderItem = orderItems.size();

        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("barCode", barCode);
        context.setVariable("deliveryAddress", deliveryAddress);
        context.setVariable("randomOrder", randomOrder);
        context.setVariable("orderItems", orderItems);
        context.setVariable("sizeOrderItem", sizeOrderItem);

        String html = templateEngine.process("order_template", context);
        return html;
    }

    public byte[] convertHtmlToPdf(String html) {
        log.info("=========== START CONVERT HTML TO PDF ============");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            // Nhúng font hỗ trợ tiếng Việt
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/DejaVuSans.ttf"),
                    "DejaVu Sans");
            builder.toStream(baos);
            builder.run();

            log.info("=========== END CONVERT HTML TO PDF ============");
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error converting HTML to PDF", e);
        }
    }
}
