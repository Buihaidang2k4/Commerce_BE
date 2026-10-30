package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;
import com.example.MyShop_API.exception.AppException;


import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.response.CartItemResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.mapper.CartItemMapper;
import com.example.MyShop_API.service.cart.ICartItemService;
import com.example.MyShop_API.service.cart.ICartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ApiPath.BASE_CART_ITEM)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemController {
    ICartItemService cartItemService;
    ICartService cartService;
    CartItemMapper cartItemMapper;
    com.example.MyShop_API.mapper.CartMapper cartMapper;

    @GetMapping(ApiPath.CART_ITEM_CART_CART_ID_CART_ITEM_CART_ITEM_ID)
    ResponseEntity<ApiResponse<CartItemResponse>> getCartItem(@PathVariable Long cartId, @PathVariable Long cartItemId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "get all cart", cartItemMapper.toResponse(cartItemService.getCartItem(cartId, cartItemId))));
    }

    @GetMapping(ApiPath.CART_ITEM_PROFILE_ID_IDS)
    ResponseEntity<ApiResponse<List<CartItemResponse>>> getCartItemIds(@Valid @RequestParam List<Long> ids, @PathVariable Long profileId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "get all cart itemIds", cartItemService.getCartItems(ids, profileId)));
    }

    @PostMapping(ApiPath.CART_ITEM_CART_ITEM_ADD_ITEM_TO_CART)
    ResponseEntity<ApiResponse<com.example.MyShop_API.dto.response.CartResponse>> addItemToCart(@RequestParam(value = "cartId", required = false) Optional<Long> cartId, @RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity) {
        try {
            // check cartId
            Long checkCartId = cartId.map(id -> cartService.getCartById(id) != null ? id : cartService.initializeNewCart()).orElseGet(() -> cartService.initializeNewCart());

            cartItemService.addItemToCart(checkCartId, productId, quantity);
            Cart cart = cartService.getCartById(checkCartId);
            return ResponseEntity.ok(new ApiResponse(200, "add cartItem success", cartMapper.toResponse(cart)));
        } catch (Exception e) {
            if (e instanceof AppException appException) throw appException;
            throw new IllegalStateException(e);
        }
    }

    @PutMapping(ApiPath.CART_ITEM_CART_CART_ID_CART_ITEM_CART_ITEM_ID_UPDATE_ITEM_QUANTITY_QUANTITY)
    ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId, @PathVariable Long cartItemId, @PathVariable Integer quantity) {
        try {
            cartItemService.updateItemQuantity(cartId, cartItemId, quantity);
            return ResponseEntity.ok(new ApiResponse(200, "update cartItem success", null));
        } catch (Exception e) {
            if (e instanceof AppException appException) throw appException;
            throw new IllegalStateException(e);
        }
    }

    @DeleteMapping(ApiPath.CART_ITEM_CART_CART_ID_CART_ITEM_CART_ITEM_ID_REMOVE_ITEM_FROM_CART)
    ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable("cartId") Long cartId, @PathVariable("cartItemId") Long cartItemId) {
        cartItemService.removeItemFromCart(cartId, cartItemId);
        return ResponseEntity.ok(new ApiResponse(200, "remove cartItem success", null));
    }

}
