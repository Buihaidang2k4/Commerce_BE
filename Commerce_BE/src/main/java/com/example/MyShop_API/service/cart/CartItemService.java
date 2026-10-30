package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.dto.response.CartItemResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.CartItem;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.CartItemMapper;
import com.example.MyShop_API.repo.CartItemRepository;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.service.product.IProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService implements ICartItemService {
    CartItemRepository cartItemRepository;
    ICartService cartService;
    IProductService productService;
    IInventoryService inventoryService;
    CartItemMapper cartItemMapper;


    @Transactional
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(productId);

        CartItem item = cart.getCartItems().stream()
                .filter(ci -> ci.getProduct().getProductId().equals(productId))
                .findFirst().orElse(null);

        int toReserve = quantity;
        if (!inventoryService.reserveStock(productId, toReserve)) {
            throw new AppException(CommonMessageUtils.INVENTORY_NOT_ENOUGH, product.getInventory().getAvailable(), quantity);
        }

        try {
            if (item == null) {
                item = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(quantity)
                        .unitPrice(product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice())
                        .build();
                item.setTotalPrice();
                cart.addItem(item);
            } else {
                item.setQuantity(item.getQuantity() + quantity);
                item.setTotalPrice();
            }

            cartService.recalcTotalPrice(cart);
            cartService.saveCart(cart);
        } catch (RuntimeException e) {
            inventoryService.cancelReservation(productId, toReserve);
            throw e;
        }
    }

    @Transactional
    @Override
    public void updateItemQuantity(Long cartId, Long cartItemId, int newQuantity) {
        Cart cart = cartService.getCartById(cartId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(CommonMessageUtils.CART_ITEM_NOT_EXISTED));

        int oldQuantity = cartItem.getQuantity();

        if (newQuantity > oldQuantity) {
            // Tăng thêm
            int diff = newQuantity - oldQuantity;
            // Đặt trước thêm
            boolean reserved = inventoryService.reserveStock(cartItem.getProduct().getProductId(), diff);
            if (!reserved)
                throw new AppException(CommonMessageUtils.INVENTORY_NOT_ENOUGH, cartItem.getProduct().getInventory().getAvailable(), newQuantity);
        } else if (newQuantity < oldQuantity) {
            int diff = oldQuantity - newQuantity;
            // Hủy vợi đơn
            inventoryService.cancelReservation(cartItem.getProduct().getProductId(), diff);
        }

        // cập nhật số lượng mới cho giỏ hàng
        cartItem.setQuantity(newQuantity);
        cartItem.setTotalPrice();

        // Cập nhật tổng giá
        cartService.recalcTotalPrice(cart);
        cartService.saveCart(cart);
    }


    @Override
    @Transactional
    public void removeItemFromCart(Long cartId, Long cartItemId) {
        Cart cart = cartService.getCartById(cartId);
        CartItem itemToRemove = getCartItem(cartId, cartItemId);

        // Trả kho
        inventoryService.cancelReservation(itemToRemove.getProduct().getProductId(), itemToRemove.getQuantity());
        cart.removeItem(itemToRemove);
        cartItemRepository.deleteByIdDirect(cartItemId);
        cartService.recalcTotalPrice(cart);
        cartService.saveCart(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItem getCartItem(Long cartId, Long cartItemId) {
        Cart cart = cartService.getCartById(cartId);

        if (cart == null)
            throw new AppException(CommonMessageUtils.CART_NOT_EXISTED);

        return cart.getCartItems()
                .stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst().orElseThrow(() -> new AppException(CommonMessageUtils.CART_ITEM_NOT_EXISTED));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(List<Long> ids, Long profileId) {
        if (ids == null || ids.isEmpty())
            return List.of();

        List<CartItem> cartItems =
                cartItemRepository.findALlByIdsAndProfile(ids, profileId);

        if (cartItems.size() != ids.size())
            throw new AppException(CommonMessageUtils.CART_ITEM_NOT_EXISTED);

        return cartItems.stream()
                .map(cartItemMapper::toResponse)
                .toList();
    }
}
