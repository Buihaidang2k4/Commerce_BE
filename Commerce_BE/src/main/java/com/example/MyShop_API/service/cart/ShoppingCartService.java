package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.repo.OrderItemRepository;
import java.util.Optional;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.mapper.ShoppingCartMapper;
import com.example.MyShop_API.repo.CartItemRepository;
import com.example.MyShop_API.repo.ShoppingCartRepository;
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
public class ShoppingCartService implements IShoppingCartService {
    private final OrderItemRepository orderItemRepository;
    com.example.MyShop_API.service.inventory.IStockBalanceService inventoryService;
    ShoppingCartRepository cartRepository;
    UserProfileRepository userProfileRepository;
    CartItemRepository cartItemRepository;
    AtomicLong generatorId = new AtomicLong(0);

    /**
     * Get all cart
     *
     * @return {@link List<ShoppingCart>}
     */
    @Override
    @Transactional(readOnly = true)
    public List<ShoppingCart> getCarts() {
        log.info("getCarts ");
        List<ShoppingCart> carts = cartRepository.findAll();
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
    public Page<ShoppingCart> getAllCarts(Pageable pageable) {
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
    public ShoppingCart getCartById(Long cartId) {
        return cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new AppException(CommonMessageConstants.CART_NOT_EXISTED));
    }

    /**
     * Get cart by userprofileId
     *
     * @param userProfileId
     * @return cart
     */
    @Override
    @Transactional(readOnly = true)
    public ShoppingCart getCartByUserProfileId(Long userProfileId) {
        return cartRepository.findByUserProfileId(userProfileId).orElseThrow(() -> new AppException(CommonMessageConstants.CART_NOT_EXISTED));
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
        ShoppingCart cart = getCartById(id);
        return cart.getSubtotalAmount();
    }

    /**
     * Create new cart
     *
     * @return cartId
     */
    @Override
    public Long initializeNewCart() {
        return cartRepository.save(new ShoppingCart()).getId();
    }

    /**
     * add cart -> UserAccount
     *
     * @param cartId
     * @param userProfileId
     * @return cart
     */
    @Override
    public ShoppingCart addCartForUserProfile(Long userProfileId, Long cartId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(()
                -> new AppException(CommonMessageConstants.USER_NOT_EXISTED));

        ShoppingCart cart = getCartById(cartId);
        cart.setUserProfileId(userProfile.getId());
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
        ShoppingCart cart = getCartById(cartId);
        for (CartItem item : Optional.ofNullable(cartId).map(cartItemRepository::findByCartId).orElseGet(List::of)) inventoryService.cancelReservation(item.getProductId(), item.getQuantity());
        cartItemRepository.hardDeleteByCartId(cartId);
        cart.setSubtotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Transactional
    public void recalcTotalPrice(ShoppingCart cart) {
        BigDecimal total = Optional.ofNullable(cart.getId()).map(cartItemRepository::findByCartId).orElseGet(List::of).stream()
                .map(ci -> ci.getLineAmount() == null ? BigDecimal.ZERO : ci.getLineAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setSubtotalAmount(total);
    }

    @Transactional
    public ShoppingCart saveCart(ShoppingCart cart) {
        return cartRepository.save(cart);
    }


    @Transactional
    public void removeSelectedItemsFromCartByItemIds(Long cartId, List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            return;
        }
        ShoppingCart cart = cartRepository.findById(cartId).orElseThrow(() -> new AppException(CommonMessageConstants.CART_NOT_EXISTED));
        for (Long id : cartItemIds) {
            if (cartItemRepository.findByIdAndCartId(id, cartId).isEmpty()) throw new AppException(CommonMessageConstants.CART_ITEM_NOT_EXISTED);
            cartItemRepository.deleteByIdDirect(id);
        }
        recalcTotalPrice(cart);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeItemAfterOrder(Order order) {
        ShoppingCart cart = Optional.ofNullable(order.getUserProfileId()).flatMap(cartRepository::findByUserProfileId).orElse(null);
        List<Long> ids = Optional.ofNullable(order.getId()).map(orderItemRepository::findByOrderId).orElseGet(List::of).stream()
                .map(OrderItem::getCartItemId)
                .filter(Objects::nonNull)
                .toList();


        cartItemRepository.deleteAllById(ids);
        recalcTotalPrice(cart);
    }

}
