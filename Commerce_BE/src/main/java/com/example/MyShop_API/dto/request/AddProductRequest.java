package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.entity.Category;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductRequest {

    @NotBlank(message = "{validation.product.name.is.required}")
    @Size(min = 7, max = 200, message = "{validation.product.name.must.contain.between.and.characters}")
    String productName;

    @Size(max = 100, message = "{validation.origin.must.contain.at.most.characters}")
    String origin;

    @NotBlank(message = "{validation.bio.is.required}")
    String bio;

    // Số > 0 hoặc có thể để NULL nếu không bắt buộc
    @Positive(message = "{validation.height.must.be.greater.than.zero}")
    Double height;

    @Positive(message = "{validation.length.must.be.greater.than.zero}")
    Double length;

    @Positive(message = "{validation.weight.must.be.greater.than.zero}")
    Double weight;

    @Positive(message = "{validation.width.must.be.greater.than.zero}")
    Double width;

    @Size(max = 500, message = "{validation.description.must.contain.at.most.characters}")
    String description;

    @NotNull(message = "{validation.category.is.required}")
    Long categoryId;

    @NotNull(message = "{validation.quantity.is.required}")
    @PositiveOrZero(message = "{validation.quantity.must.be.nonnegative}")
    Integer quantity;

    @NotNull(message = "{validation.product.price.is.required}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{validation.price.must.be.greater.than.zero}")
    BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "{validation.discount.must.be.nonnegative}")
    @DecimalMax(value = "100.0", inclusive = true, message = "{validation.discount.must.not.exceed.100}")
    BigDecimal discount;

    Integer soldCount = 0;
    Integer reviewCount = 0;
    Double avgRating = 0.0;
    LocalDate createAt = LocalDate.now();

}
