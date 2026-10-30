package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ICartService {
    List<Cart> getCarts();

    Page<Cart> getAllCarts(Pageable pageable);

    Cart getCartById(Long cartId);

    Cart getCartByUserProfileId(Long userProfileId);

    BigDecimal getTotalPrice(Long id);

    Long initializeNewCart();

    Cart addCartForUserProfile(Long userProfileId, Long cartId);

    void clearCart(Long cartId);

    void recalcTotalPrice(Cart cart);

    Cart saveCart(Cart cart);

    void removeSelectedItemsFromCartByItemIds(Long cartId, List<Long> cartItemIds);

    void removeItemAfterOrder(Order order);
}
