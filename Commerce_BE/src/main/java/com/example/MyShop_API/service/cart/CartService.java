package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.CartMapper;
import com.example.MyShop_API.repo.CartItemRepository;
import com.example.MyShop_API.repo.CartRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService implements ICartService {
    CartRepository cartRepository;
    UserProfileRepository userProfileRepository;
    CartItemRepository cartItemRepository;
    AtomicLong generatorId = new AtomicLong(0);

    /**
     * Get all cart
     *
     * @return {@link List<Cart>}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Cart> getCarts() {
        log.info("getCarts ");
        List<Cart> carts = cartRepository.findAll();
        return carts;
    }

    /**
     * Get cart by page
     *
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Cart> getAllCarts(Pageable pageable) {
        return cartRepository.findAll(pageable);
    }

    /**
     * Get card by cardId
     *
     * @param cartId
     * @return cart
     */
    @Override
    @Transactional(readOnly = true)
    public Cart getCartById(Long cartId) {
        return cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new AppException(CommonMessageUtils.CART_NOT_EXISTED));
    }

    /**
     * Get cart by userprofileId
     *
     * @param userProfileId
     * @return cart
     */
    @Override
    @Transactional(readOnly = true)
    public Cart getCartByUserProfileId(Long userProfileId) {
        return cartRepository.findByUserProfileId(userProfileId).orElseThrow(() -> new AppException(CommonMessageUtils.CART_NOT_EXISTED));
    }


    /**
     * Get total price
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCartById(id);
        return cart.getTotalPrice();
    }

    /**
     * Create new cart
     *
     * @return cartId
     */
    @Override
    public Long initializeNewCart() {
        Cart newCart = new Cart();
        Long cartId = generatorId.incrementAndGet();
        newCart.setCartId(cartId);
        return cartRepository.save(newCart).getCartId();
    }

    /**
     * add cart -> User
     *
     * @param cartId
     * @param userProfileId
     * @return cart
     */
    @Override
    public Cart addCartForUserProfile(Long userProfileId, Long cartId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(()
                -> new AppException(CommonMessageUtils.USER_NOT_EXISTED));

        Cart cart = getCartById(cartId);
        cart.setProfile(userProfile);
        return cartRepository.save(cart);
    }

    /**
     * clear cart
     *
     * @param cartId
     */
    @Override
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = getCartById(cartId);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartItemRepository.hardDeleteByCartId(cartId);
        cartItemRepository.flush();

        // Then delete the cart
        cartRepository.deleteById(cartId);
        cartRepository.flush();
    }

    @Transactional
    public void recalcTotalPrice(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(ci -> ci.getTotalPrice() == null ? BigDecimal.ZERO : ci.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
    }

    @Transactional
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }


    @Transactional
    public void removeSelectedItemsFromCartByItemIds(Long cartId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return;
        }
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new AppException(CommonMessageUtils.CART_NOT_EXISTED));
        cartItemIds.forEach(cartItemRepository::deleteByIdDirect);
        cart.updateTotalAmount();
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeItemAfterOrder(Order order) {
        Cart cart = order.getProfile().getCart();
        List<Long> ids = order.getOrderItems().stream()
                .map(OrderItem::getCartItemId)
                .filter(Objects::nonNull)
                .toList();

        cart.getCartItems().removeIf(ci -> ids.contains(ci.getCartItemId()));
        cartItemRepository.deleteAllById(ids);
        cart.updateTotalAmount();
    }

}
