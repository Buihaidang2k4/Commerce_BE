package com.example.MyShop_API.config.payment;

import com.example.MyShop_API.utils.VnpayUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VnpayConfig {
    @Getter
    @Value("${payment.vnPay.url}")
    String vnp_PayUrl;
    @Value("${payment.vnPay.returnUrl}")
    String vnp_ReturnUrl;
    @Value("${payment.vnPay.tmnCode}")
    String vnp_TmnCode;
    @Getter
    @Value("${payment.vnPay.secretKey}")
    String secretKey;
    @Value("${payment.vnPay.version}")
    String vnp_Version;
    @Value("${payment.vnPay.command}")
    String vnp_Command;
    @Value("${payment.vnPay.orderType}")
    String orderType;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", this.vnp_Version);
        vnp_Params.put("vnp_Command", this.vnp_Command);
        vnp_Params.put("vnp_TmnCode", this.vnp_TmnCode);
        vnp_Params.put("vnp_CurrCode", "VND");
//        vnp_Params.put("vnp_TxnRef", VnpayUtil.getRandomNumber(8));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + VnpayUtil.getRandomNumber(8));
        vnp_Params.put("vnp_OrderType", this.orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnp_Params;
    }
}
