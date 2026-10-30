package com.example.MyShop_API.service.model;

import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;

/** Explicit application-side lookups; entities contain only scalar IDs. */
@Component
@RequiredArgsConstructor
@lombok.Getter
public class ModelLookup {
    private final ProductRepository products;
    private final UserRepository users;
    private final UserProfileRepository profiles;
    private final CartRepository carts;
    private final CartItemRepository cartItems;
    private final OrderRepository orders;
    private final OrderItemRepository orderItems;
    private final PaymentRepository payments;
    private final CouponRepository coupons;
    private final CategoryRepository categories;
    private final ImageRepository images;
    private final InventoryRepository inventories;
    private final AddressRepository addresses;
    private final OrderDeliveryAddressRepository deliveryAddresses;
    private final OrderStatusHistoryRepository histories;
    private final RoleRepository roles;
    private final UserRoleRepository userRoles;
    private final CouponCategoryRepository couponCategories;
    private final CouponProductRepository couponProducts;

    public Product product(Long id) { return id == null ? null : products.findById(id).orElse(null); }
    public User user(Long id) { return id == null ? null : users.findById(id).orElse(null); }
    public UserProfile profile(Long id) { return id == null ? null : profiles.findById(id).orElse(null); }
    public UserProfile profileForUser(Long id) { return id == null ? null : profiles.findByUserId(id).orElse(null); }
    public Cart cartForProfile(Long id) { return id == null ? null : carts.findByProfileId(id).orElse(null); }
    public Order order(Long id) { return id == null ? null : orders.findById(id).orElse(null); }
    public Payment payment(Long id) { return id == null ? null : payments.findById(id).orElse(null); }
    public Coupon coupon(Long id) { return id == null ? null : coupons.findById(id).orElse(null); }
    public Category category(Long id) { return id == null ? null : categories.findById(id).orElse(null); }
    public Inventory inventory(Long productId) { return productId == null ? null : inventories.findByProductId(productId).orElse(null); }
    public List<CartItem> cartItems(Long id) { return id == null ? List.of() : cartItems.findByCartId(id); }
    public List<OrderItem> orderItems(Long id) { return id == null ? List.of() : orderItems.findByOrderId(id); }
    public OrderDeliveryAddress deliveryAddress(Long id) { return id == null ? null : deliveryAddresses.findOrderDeliveryAddressByOrderId(id); }
    public List<Image> images(Long id) { return id == null ? List.of() : images.findImageByProductId(id); }
    public List<Address> addresses(Long id) { return id == null ? List.of() : addresses.findByProfileId(id); }
    public Set<Role> roles(Long userId) {
        if (userId == null) return Set.of();
        return new HashSet<>(roles.findAllById(userRoles.findByUserId(userId).stream().map(UserRole::getRoleName).toList()));
    }
    @org.springframework.transaction.annotation.Transactional
    public void replaceRoles(Long userId, Collection<Role> values) {
        users.findByIdForUpdate(userId).orElseThrow();
        userRoles.deleteByUserId(userId);
        userRoles.flush();
        userRoles.saveAll(values.stream().map(role -> UserRole.builder().userId(userId).roleName(role.getRoleName()).build()).toList());
    }
    public Set<Category> couponCategories(Long id) { return id == null ? Set.of() : new HashSet<>(categories.findAllById(couponCategories.findByCouponId(id).stream().map(CouponCategory::getCategoryId).toList())); }
    public Set<Product> couponProducts(Long id) { return id == null ? Set.of() : new HashSet<>(products.findAllById(couponProducts.findByCouponId(id).stream().map(CouponProduct::getProductId).toList())); }
    public void replaceCouponCategories(Long id, Collection<Category> values) {
        coupons.findByIdForUpdate(id).orElseThrow();
        couponCategories.deleteByCouponId(id); couponCategories.flush();
        couponCategories.saveAll(values.stream().map(c -> CouponCategory.builder().couponId(id).categoryId(c.getCategoryId()).build()).toList());
    }
    public void replaceCouponProducts(Long id, Collection<Product> values) {
        coupons.findByIdForUpdate(id).orElseThrow();
        couponProducts.deleteByCouponId(id); couponProducts.flush();
        couponProducts.saveAll(values.stream().map(p -> CouponProduct.builder().couponId(id).productId(p.getProductId()).build()).toList());
    }
}
