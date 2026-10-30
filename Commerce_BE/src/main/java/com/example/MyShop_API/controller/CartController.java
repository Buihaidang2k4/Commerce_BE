package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPath;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.mapper.CartMapper;
import com.example.MyShop_API.service.cart.CartService;
import com.example.MyShop_API.service.cart.ICartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiPath.BASE_CART)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    ICartService cartService;
    CartMapper cartMapper;

    @GetMapping(ApiPath.CART_ALL)
    ResponseEntity<ApiResponse> getCarts() {
        List<Cart> carts = cartService.getCarts();

        return ResponseEntity.ok(new ApiResponse<>(200, "success", cartMapper.toResponseList(carts)));
    }

    @GetMapping(ApiPath.CART_PAGE)
    ResponseEntity<ApiResponse> getCarts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cartId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Cart> carts = cartService.getAllCarts(pageable);

        Map<String, Object> res = new HashMap<>();
        res.put("content", carts.getContent()); // data
        res.put("currentPage", carts.getNumber());
        res.put("totalItems", carts.getTotalElements());
        res.put("totalPages", carts.getTotalPages());
        res.put("size", carts.getSize()); // number one page
        res.put("sortBy", sortBy);

        return ResponseEntity.ok(new ApiResponse(200, "success", res));
    }

    @GetMapping(ApiPath.CART_CART_CARD_ID)
    ResponseEntity<ApiResponse<Cart>> getCart(@PathVariable Long cardId) {
        return ResponseEntity.ok(new ApiResponse<>(200, "success", cartService.getCartById(cardId)));
    }

    @GetMapping(ApiPath.CART_CART_USER_PROFILE_PROFILE_ID)
    ResponseEntity<ApiResponse<CartResponse>> getCartByUserProfile(@PathVariable Long profileId) {
        CartResponse response = cartMapper.toResponse(cartService.getCartByUserProfileId(profileId));
        return ResponseEntity.ok(new ApiResponse<>(200, "success", response));
    }

    @PostMapping(ApiPath.CART_USER_PROFILE_PROFILE_ID_CART_CART_ID_ADD_CART_TO_USER_PROFILE)
    ResponseEntity<ApiResponse> addCartToUserProfile(@PathVariable Long profileId, @PathVariable Long cartId) {
        return ResponseEntity.ok(new ApiResponse(200, "add cart to user-profile success", cartService.addCartForUserProfile(profileId, cartId)));
    }

    @DeleteMapping(ApiPath.CART_CART_CART_ID_CLEAR)
    ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.ok(new ApiResponse(200, "Delete cart success ", null));
    }

    @GetMapping(ApiPath.CART_CART_CART_ID_TOTAL_PRICE)
    ResponseEntity<ApiResponse> getTotalPrice(@PathVariable Long cartId) {
        BigDecimal totalPrice = cartService.getTotalPrice(cartId);
        return ResponseEntity.ok(new ApiResponse(200, "Total Price", totalPrice));
    }
}
