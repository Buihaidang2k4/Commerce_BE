package com.example.MyShop_API.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productId;

    @Column(unique = true)
    String productName;

    @Column(columnDefinition = "TEXT")
    String description;
    BigDecimal price;
    BigDecimal specialPrice;
    BigDecimal discount;

    @Column(columnDefinition = "TEXT")
    String bio;

    @Column(unique = true)
    String slug;
    Double height;
    Double length;
    Double weight;
    Double width;

    String origin;
    Integer soldCount;
    Integer reviewCount;
    Double avgRating;
    LocalDate createAt;
    LocalDate updateAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Image> images;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonIgnore
    List<CartItem> cartItems = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    Inventory inventory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Review> reviews = new ArrayList<>();

    public Product(String productName, String description, BigDecimal price, BigDecimal discount, BigDecimal specialPrice, Category category) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.discount = discount;
        this.specialPrice = specialPrice;
        this.category = category;
    }
}
