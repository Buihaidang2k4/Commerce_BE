package com.example.MyShop_API.service.coupon;

import com.example.MyShop_API.constants.CouponScope;
import com.example.MyShop_API.constants.DiscountType;
import com.example.MyShop_API.anotation.AdminOnly;
import com.example.MyShop_API.dto.request.CreateCouponRequest;
import com.example.MyShop_API.dto.request.UpdateCouponRequest;
import com.example.MyShop_API.dto.response.CategoryResponse;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.CouponMapper;
import com.example.MyShop_API.repo.CategoryRepository;
import com.example.MyShop_API.repo.CouponRepository;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.repo.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.example.MyShop_API.constants.CouponScope.*;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CouponService implements ICouponService {
    private final com.example.MyShop_API.service.model.ModelLookup models;
    CouponRepository couponRepository;
    CouponMapper couponMapper;
    OrderRepository orderRepository;
    CategoryRepository categoryRepository;
    ProductRepository productRepository;

    // ============= ALL COUPONS ==============
    @Transactional(readOnly = true)
    public List<Coupon> getCoupons() {
        return couponRepository.findAll();
    }

    // ================== CREATE COUPON (ADMIN) ==========================
    @AdminOnly
    @Override
    @Transactional
    public Coupon createCoupon(CreateCouponRequest request) {

        validateCreateCoupon(request);

        Coupon coupon = couponMapper.toCoupon(request);

        coupon.setStartsAt(
                request.getStartDate() != null
                        ? request.getStartDate()
                        : Instant.now()
        );

        coupon.setRedeemedCount(0);

        // generate unique code
        String code;
        do {
            code = generateCode(request.getCode());
        } while (couponRepository.existsByCode(code));
        coupon.setCode(code);

        couponRepository.save(coupon);
        // bind scope
        switch (request.getScope()) {

            case "GLOBAL" -> {
                models.replaceCouponCategories(coupon.getId(), List.of());
                models.replaceCouponProducts(coupon.getId(), List.of());
            }

            case "CATEGORY" -> {
                List<Category> categories =
                        categoryRepository.findAllById(request.getCategoryIds());

                if (categories.size() != request.getCategoryIds().size()) {
                    throw new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED);
                }

                models.replaceCouponCategories(coupon.getId(), categories);
            }

            case "PRODUCT" -> {
                List<Product> products =
                        productRepository.findAllById(request.getProductIds());

                if (products.size() != request.getProductIds().size()) {
                    throw new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED);
                }

                models.replaceCouponProducts(coupon.getId(), products);
            }
        }

        return couponRepository.save(coupon);
    }


    // ================ LIST AVAILABLE COUPONS ===========================
    @Override
    @Transactional(readOnly = true)
    public List<Coupon> getAvailableCoupons(BigDecimal orderTotal) {
        return couponRepository.findAvailableCoupons(orderTotal, Instant.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Coupon> getAvailableCoupons() {
        return couponRepository.findCouponByEnabledTrue();
    }


    // =============== APPLY COUPON TO ORDER =================
    @Override
    public BigDecimal applyCouponToOrder(String couponCode,
                                         BigDecimal orderTotal,
                                         Order order,
                                         UserProfile profile) {
        log.info("================== START APPLY COUPON ==================");
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndEnabledTrue(couponCode)
                .filter(Coupon::isUsable)
                .filter(c -> c.getMinimumOrderAmount() == null || orderTotal.compareTo(c.getMinimumOrderAmount()) >= 0)
                .orElseThrow(() -> new AppException(CommonMessageUtils.COUPON_INVALID));

        // Check limit per user
        if (coupon.isLimitPerUser()) {
            int usedCount = orderRepository.countByUserProfileIdAndCouponId(
                    profile.getId(), coupon.getId());

            if (usedCount >= coupon.getPerUserUsageLimit())
                throw new AppException(CommonMessageUtils.COUPON_LIMIT_PER_USER_EXCEEDED);
        }
        // valid scope
        validateCouponScope(coupon, order);

        // Calculate discount
        BigDecimal discount = calculateDiscount(coupon, orderTotal);

        // apply discount
        order.setCouponId(coupon.getId());

        coupon.incrementUsedCount();
        couponRepository.save(coupon);

        log.info("Coupon {} applied to order {}. Discount: {}", couponCode, order.getId(), discount);
        log.info("================= END APPLY COUPON ================");
        return discount;
    }

    @Override
    @Transactional
    public void updateCoupon(Long couponId, UpdateCouponRequest request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.COUPON_NOT_EXISTED));

        Instant now = Instant.now();

        // 1. Không cho update coupon đã hết hạn
        if (coupon.getEndsAt().isBefore(now))
            throw new AppException(CommonMessageUtils.COUPON_EXPIRED);

        boolean hasBeenUsed = coupon.getRedeemedCount() != null && coupon.getRedeemedCount() > 0;
        boolean hasStarted = coupon.getStartsAt() != null && coupon.getStartsAt().isBefore(now);


        // 2. expiryDate chỉ cho update khi chưa bắt đầu
        if (request.getStartDate() != null) {
            if (hasStarted)
                throw new AppException(CommonMessageUtils.CANNOT_UPDATE_START_DATE);

            if (request.getExpiryDate() != null &&
                    request.getExpiryDate().isBefore(request.getStartDate())) {
                throw new AppException(CommonMessageUtils.EXPIRY_BEFORE_START);
            }

            coupon.setStartsAt(request.getStartDate());
        }

        // 3. expiryDate chỉ cho gia hạn
        if (request.getExpiryDate() != null) {
            if (request.getExpiryDate().isBefore(coupon.getEndsAt()))
                throw new AppException(CommonMessageUtils.CANNOT_SHORTEN_EXPIRY);

            coupon.setEndsAt(request.getExpiryDate());
        }


        // 4. enable
        if (request.getEnabled() != null) {
            if (Boolean.TRUE.equals(request.getEnabled()) && coupon.getEndsAt().isBefore(now))
                throw new AppException(CommonMessageUtils.CANNOT_ENABLE_EXPIRED_COUPON);

            coupon.setEnabled(request.getEnabled());
        }

        // 5. Fill chỉ khi CHƯA dùng
        if (!hasBeenUsed) {

            if (request.getMinOrderValue() != null) {
                coupon.setMinimumOrderAmount(request.getMinOrderValue());
            }

            if (request.getMaxDiscountAmount() != null) {
                coupon.setMaximumDiscountAmount(request.getMaxDiscountAmount());
            }

        } else {
            // Coupon đã dùng → không cho sửa rule
            if (request.getMinOrderValue() != null
                    || request.getMaxDiscountAmount() != null
                    || request.getStartDate() != null) {

                throw new AppException(CommonMessageUtils.CANNOT_UPDATE_COUPON_AFTER_USED);
            }
        }

        couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(Long couponId) {

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.COUPON_NOT_EXISTED));

        if (coupon.getRedeemedCount() > 0) {
            throw new AppException(CommonMessageUtils.COUPON_ALREADY_USED);
        }

        Instant now = Instant.now();
        if (coupon.isEnabled()
                && !coupon.getStartsAt().isAfter(now)
                && !coupon.getEndsAt().isBefore(now)) {
            throw new AppException(CommonMessageUtils.CANNOT_DELETE_ACTIVE_COUPON);
        }

        models.replaceCouponCategories(coupon.getId(), List.of());
        models.replaceCouponProducts(coupon.getId(), List.of());
        couponRepository.delete(coupon);
    }

    private String generateCode(String prefix) {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        return prefix.toUpperCase() + "-" + random;
    }

    private void validateCreateCoupon(CreateCouponRequest request) {

    /* =====================================================
       1. Validate discount
       ===================================================== */
        switch (request.getDiscountType()) {

            case "PERCENTAGE":
                validatePercentageDiscount(request);
                break;

            case "FIXED_AMOUNT":
                validateFixedAmountDiscount(request);
                break;

            default:
                throw new AppException(CommonMessageUtils.COUPON_INVALID_TYPE);
        }

    /* =====================================================
       2. Validate date range
       ===================================================== */
        if (request.getStartDate() != null &&
                request.getExpiryDate() != null &&
                request.getStartDate().isAfter(request.getExpiryDate())) {
            throw new AppException(CommonMessageUtils.COUPON_INVALID_DATE_RANGE);
        }

    /* =====================================================
       3. Validate usage limit
       ===================================================== */
        if (request.getUsageLimit() != null &&
                request.getUsageLimit() < 1) {
            throw new AppException(CommonMessageUtils.COUPON_INVALID_USAGE_LIMIT);
        }

    /* =====================================================
       4. Validate per-user
       ===================================================== */
        if (request.isLimitPerUser() &&
                (request.getMaxUsesPerUser() == null || request.getMaxUsesPerUser() < 1)) {
            throw new AppException(CommonMessageUtils.COUPON_INVALID_MAX_USES_PER_USER);
        }

    /* =====================================================
       5. Validate scope
       ===================================================== */
        switch (request.getScope()) {

            case "GLOBAL":
                validateGlobalScope(request);
                break;

            case "CATEGORY":
                validateCategoryScope(request);
                break;

            case "PRODUCT":
                validateProductScope(request);
                break;

            default:
                throw new AppException(CommonMessageUtils.COUPON_INVALID_SCOPE);
        }
    }

    private void validatePercentageDiscount(CreateCouponRequest request) {
        if (request.getDiscountPercent() == null) {
            throw new AppException(CommonMessageUtils.COUPON_INVALID_PERCENTAGE);
        }

        if (request.getDiscountAmount() != null) {
            throw new AppException(CommonMessageUtils.COUPON_INVALID_FIXED_AMOUNT);
        }
    }


    private void validateFixedAmountDiscount(CreateCouponRequest request) {

        if (request.getDiscountAmount() == null) {
            throw new AppException(CommonMessageUtils.COUPON_INVALID_FIXED_AMOUNT);
        }

        if (request.getDiscountPercent() != null) {
            throw new AppException(CommonMessageUtils.COUPON_INVALID_PERCENTAGE);
        }

    }


    private void validateGlobalScope(CreateCouponRequest request) {

        if (!isEmpty(request.getCategoryIds()) ||
                !isEmpty(request.getProductIds())) {
            throw new AppException(CommonMessageUtils.COUPON_SCOPE_CONFLICT);
        }
    }

    private void validateCategoryScope(CreateCouponRequest request) {

        if (isEmpty(request.getCategoryIds())) {
            throw new AppException(CommonMessageUtils.COUPON_CATEGORY_REQUIRED);
        }

        if (!isEmpty(request.getProductIds())) {
            throw new AppException(CommonMessageUtils.COUPON_SCOPE_CONFLICT);
        }
    }

    private void validateProductScope(CreateCouponRequest request) {

        if (isEmpty(request.getProductIds())) {
            throw new AppException(CommonMessageUtils.COUPON_PRODUCT_REQUIRED);
        }

        if (!isEmpty(request.getCategoryIds())) {
            throw new AppException(CommonMessageUtils.COUPON_SCOPE_CONFLICT);
        }
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    // check match scope
    private void validateCouponScope(Coupon coupon, Order order) {

        switch (coupon.getScope()) {

            case "GLOBAL":
                return;

            case "CATEGORY":
                boolean matchCategory = models.orderItems(order.getId()).stream()
                        .map(oi -> models.category(models.product(oi.getProductId()).getCategoryId()))
                        .anyMatch(models.couponCategories(coupon.getId())::contains);

                if (!matchCategory) {
                    throw new AppException(CommonMessageUtils.COUPON_CATEGORY_NOT_APPLICABLE);
                }
                break;

            case "PRODUCT":
                boolean matchProduct = models.orderItems(order.getId()).stream()
                        .map(oi -> models.product(oi.getProductId()))
                        .anyMatch(models.couponProducts(coupon.getId())::contains);

                if (!matchProduct) {
                    throw new AppException(CommonMessageUtils.COUPON_PRODUCT_NOT_APPLICABLE);
                }
                break;

            default:
                throw new AppException(CommonMessageUtils.COUPON_INVALID_SCOPE);
        }
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderTotal) {

        BigDecimal discount;

        switch (coupon.getDiscountType()) {

            case "PERCENTAGE":
                discount = orderTotal
                        .multiply(coupon.getDiscountPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                if (coupon.getMaximumDiscountAmount() != null
                        && discount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
                    discount = coupon.getMaximumDiscountAmount();
                }
                break;

            case "FIXED_AMOUNT":
                discount = coupon.getDiscountAmount();
                break;

            default:
                throw new AppException(CommonMessageUtils.COUPON_INVALID_TYPE);
        }

        return discount;
    }

}
