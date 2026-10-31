package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.ShoppingCart;
import com.example.MyShop_API.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IShoppingCartService {
    List<ShoppingCart> getCarts();

    Page<ShoppingCart> getAllCarts(Pageable pageable);

    ShoppingCart getCartById(Long cartId);

    ShoppingCart getCartByUserProfileId(Long userProfileId);

    BigDecimal getTotalPrice(Long id);

    Long initializeNewCart();

    ShoppingCart addCartForUserProfile(Long userProfileId, Long cartId);

    void clearCart(Long cartId);

    void recalcTotalPrice(ShoppingCart cart);

    ShoppingCart saveCart(ShoppingCart cart);

    void removeSelectedItemsFromCartByItemIds(Long cartId, List<Long> cartItemIds);

    void removeItemAfterOrder(Order order);
}
