package com.example.MyShop_API.service.product;

import com.example.MyShop_API.entity.Inventory;
import com.example.MyShop_API.entity.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProductSpecification {

    public static Specification<Product> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) return null;
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("productName")), like),
                    cb.like(cb.lower(root.get("slug")), like)
            );
        };
    }

    public static Specification<Product> hasCategoryName(String categoryName) {
        return (root, query, cb) ->
                StringUtils.hasText(categoryName)
                        ? cb.equal(root.get("category").get("categoryName"), categoryName)
                        : null;
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }

    // loi
    public static Specification<Product> hasDiscount(Boolean hasDiscount) {
        return (root, query, cb) -> {
            if (hasDiscount == null) return null;
            return hasDiscount
                    ? cb.greaterThan(root.get("discount"), BigDecimal.ZERO)
                    : null;
        };
    }

    // ban chay
    public static Specification<Product> bestSeller(Boolean bestSeller) {
        return (root, query, cb) ->
                Boolean.TRUE.equals(bestSeller)
                        ? cb.greaterThan(root.get("soldCount"), 0)
                        : null;
    }


    public static Specification<Product> minRating(Double rating) {
        return (root, query, cb) ->
                rating == null ? null : cb.greaterThanOrEqualTo(root.get("avgRating"), rating);
    }

    public static Specification<Product> origin(String origin) {
        return (root, query, cb) ->
                StringUtils.hasText(origin)
                        ? cb.equal(cb.lower(root.get("origin")), origin.toLowerCase())
                        : null;
    }

    public static Specification<Product> inStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null) return null;

            Join<Product, Inventory> inventory =
                    root.join("inventory", JoinType.LEFT);

            return inStock
                    ? cb.greaterThan(inventory.get("available"), 0)
                    : cb.equal(inventory.get("available"), 0);
        };
    }

    public static Specification<Product> createdBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            if (from == null) return cb.lessThanOrEqualTo(root.get("createAt"), to);
            if (to == null) return cb.greaterThanOrEqualTo(root.get("createAt"), from);
            return cb.between(root.get("createAt"), from, to);
        };
    }
}
