package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.constants.CouponScope;
import com.example.MyShop_API.constants.DiscountType;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCouponRequest {

    String code; // để trống → tự sinh

    @NotNull(message = "{validation.scope.discount.not.null}")
    String scope;

    @NotNull(message = "{validation.discount.type.is.required}")
    String discountType;

    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.discount.percent.must.be.0}")
    @DecimalMax(value = "100.0", message = "{validation.discount.percent.must.be.100}")
    BigDecimal discountPercent;

    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.discount.amount.must.be.0}")
    BigDecimal discountAmount;

    @NotNull(message = "{validation.maxdiscountamount.not.null}")
    @DecimalMin(value = "0.0", message = "{validation.max.discount.amount.must.be.0}")
    BigDecimal maxDiscountAmount;

    @NotNull(message = "{validation.minordervalue.not.null}")
    @DecimalMin(value = "0.0", message = "{validation.min.order.value.must.be.0}")
    BigDecimal minOrderValue;

    @FutureOrPresent(message = "{validation.start.date.must.be.now.or.in.the.future}")
    LocalDateTime startDate;

    @NotNull(message = "{validation.expirydate.not.null}")
    @Future(message = "{validation.expiry.date.must.be.in.the.future}")
    LocalDateTime expiryDate;

    Integer usageLimit;           // null = không giới hạn
    boolean limitPerUser = true;  // mặc định bật
    Integer maxUsesPerUser = 1;   // mặc định 1 lần/user

    @Builder.Default
    boolean enabled = true;
    
    List<Long> categoryIds;
    List<Long> productIds;
}
