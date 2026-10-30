package com.example.MyShop_API.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long cartId;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "profile_id")
    UserProfile profile;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    List<CartItem> cartItems = new ArrayList<>();

    BigDecimal totalPrice = BigDecimal.ZERO;

    public void addItem(CartItem item) {
        Product product = item.getProduct();
        item.setUnitPrice(product.getSpecialPrice() != null ?
                product.getSpecialPrice() : product.getPrice());
        item.setCart(this);
        this.cartItems.add(item);
        updateTotalAmount();
    }

    public void removeItem(CartItem item) {
        if (item == null) return;
        item.setCart(null);
        this.cartItems.remove(item);
        updateTotalAmount();
    }

    public void updateTotalAmount() {
        this.totalPrice = cartItems.stream().map(item ->
        {
            BigDecimal unitPrice = item.getUnitPrice();
            if (unitPrice == null) {
                return BigDecimal.ZERO;
            }
            return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
