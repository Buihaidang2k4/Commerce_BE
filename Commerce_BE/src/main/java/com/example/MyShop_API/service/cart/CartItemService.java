package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.dto.response.CartItemResponse;
import com.example.MyShop_API.entity.*;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.CartItemMapper;
import com.example.MyShop_API.repo.*;
import com.example.MyShop_API.service.inventory.IStockBalanceService;
import com.example.MyShop_API.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository cartRepository;
    private final IShoppingCartService cartService;
    private final IProductService productService;
    private final IStockBalanceService inventoryService;
    private final CartItemMapper cartItemMapper;

    private ShoppingCart lockCart(Long cartId) {
        return cartRepository.findByIdForUpdate(cartId).orElseThrow(() -> new AppException(CommonMessageConstants.CART_NOT_EXISTED));
    }

    @Override
    @Transactional
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        if (quantity <= 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        ShoppingCart cart = lockCart(cartId);
        Product product = productService.getProductById(productId);
        if (!inventoryService.reserveStock(productId, quantity)) throw new AppException(CommonMessageConstants.PRODUCT_OUT_OF_STOCK);
        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .orElseGet(() -> CartItem.builder().cartId(cartId).productId(productId).quantity(0).build());
        item.setQuantity(item.getQuantity() + quantity);
        item.setUnitPrice(product.getSpecialPrice() == null ? product.getPrice() : product.getSpecialPrice());
        item.recalculateLineAmount();
        cartItemRepository.save(item);
        cartService.recalcTotalPrice(cart);
        cartService.saveCart(cart);
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long cartId, Long cartItemId, int newQuantity) {
        if (newQuantity <= 0) throw new AppException(CommonMessageConstants.VALIDATION_ERROR);
        ShoppingCart cart = lockCart(cartId);
        CartItem item = getCartItem(cartId, cartItemId);
        int difference = newQuantity - item.getQuantity();
        if (difference > 0 && !inventoryService.reserveStock(item.getProductId(), difference)) throw new AppException(CommonMessageConstants.PRODUCT_OUT_OF_STOCK);
        if (difference < 0) inventoryService.cancelReservation(item.getProductId(), -difference);
        item.setQuantity(newQuantity);
        item.recalculateLineAmount();
        cartItemRepository.save(item);
        cartService.recalcTotalPrice(cart);
        cartService.saveCart(cart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long cartId, Long cartItemId) {
        ShoppingCart cart = lockCart(cartId);
        CartItem item = getCartItem(cartId, cartItemId);
        inventoryService.cancelReservation(item.getProductId(), item.getQuantity());
        cartItemRepository.deleteByIdDirect(cartItemId);
        cartService.recalcTotalPrice(cart);
        cartService.saveCart(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartItem getCartItem(Long cartId, Long cartItemId) {
        return cartItemRepository.findByIdAndCartId(cartItemId, cartId)
                .orElseThrow(() -> new AppException(CommonMessageConstants.CART_ITEM_NOT_EXISTED));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(List<Long> ids, Long profileId) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<CartItem> items = cartItemRepository.findALlByIdsAndProfile(ids, profileId);
        if (items.size() != ids.stream().distinct().count()) throw new AppException(CommonMessageConstants.CART_ITEM_NOT_EXISTED);
        return items.stream().map(cartItemMapper::toResponse).toList();
    }
}
