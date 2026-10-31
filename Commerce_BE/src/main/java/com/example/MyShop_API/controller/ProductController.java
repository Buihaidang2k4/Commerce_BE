package com.example.MyShop_API.controller;

import com.example.MyShop_API.constants.ApiPathConstants;


import com.example.MyShop_API.dto.request.productSearch.AdminProductSearchCondition;
import com.example.MyShop_API.dto.request.productSearch.UserProductSearchCondition;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.mapper.ProductMapper;
import com.example.MyShop_API.service.product.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping(ApiPathConstants.BASE_PRODUCT)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    IProductService productService;
    ProductMapper productMapper;

    @GetMapping(ApiPathConstants.PRODUCT_SEARCH)
    public Page<ProductResponse> searchForUser(
            @Valid @ModelAttribute UserProductSearchCondition condition,
            @PageableDefault(page = 0, size = 20, sort = "price", direction = Sort.Direction.ASC) Pageable pageable) {
        return productService.searchProductsForUser(condition, pageable)
                .map(productMapper::toResponse);
    }

    @GetMapping(ApiPathConstants.PRODUCT_ADMIN_SEARCH)
    public Page<ProductResponse> searchForAdmin(
            @Valid @ModelAttribute AdminProductSearchCondition condition,
            Pageable pageable) {
        return productService.searchProductsForAdmin(condition, pageable)
                .map(productMapper::toResponse);
    }


    @GetMapping(ApiPathConstants.PRODUCT_CATEGORY_BY_CATEGORY_NAME)
    ResponseEntity<ApiResponse<?>> getProductByCategoryName(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {
            Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Product> products = productService.searchProductByCategory(categoryName, pageable);
            Page<ProductResponse> productRes = products.map(productMapper::toResponse);

            Map<String, Object> res = new HashMap<>();
            res.put("content", productRes.getContent());
            res.put("size", productRes.getNumberOfElements());
            res.put("direction", direction);
            res.put("currentPage", productRes.getNumber());
            res.put("totalItems", productRes.getTotalElements());
            res.put("totalPages", productRes.getTotalPages());
            res.put("sortBy", sortBy);


            return ResponseEntity.ok(new ApiResponse<>(200, "Get products by category name successfully!", res));
        } catch (AppException e) {
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "get by list productIds")
    ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByProductIds(@RequestParam List<Long> productIds) {
        try {
            return ResponseEntity.ok(new ApiResponse(200, "Get all products successfully!", productService.getProductsByCouponIds(productIds)));
        } catch (AppException e) {
            throw e;
        }
    }

    @GetMapping(ApiPathConstants.PRODUCT_ALL)
    ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts() {
        try {
            List<ProductResponse> products = productMapper.toResponseList(productService.getProducts());
            return ResponseEntity.ok(new ApiResponse(200, "Get all products successfully!", products));
        } catch (AppException e) {
            throw e;
        }
    }

    @GetMapping(ApiPathConstants.PRODUCT_PAGE)
    ResponseEntity<ApiResponse<?>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productService.getProducts(pageable);
        Page<ProductResponse> productRes = products.map(productMapper::toResponse);

        Map<String, Object> res = new HashMap<>();
        res.put("content", productRes.getContent());
        res.put("size", productRes.getNumberOfElements());
        res.put("direction", direction);
        res.put("currentPage", productRes.getNumber());
        res.put("totalItems", productRes.getTotalElements());
        res.put("totalPages", productRes.getTotalPages());
        res.put("sortBy", sortBy);

        return ResponseEntity.ok(new ApiResponse(200, "success", res));
    }

    @GetMapping(ApiPathConstants.PRODUCT_PRODUCT_PRODUCT_ID)
    ResponseEntity<ApiResponse<?>> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ResponseEntity.ok(new ApiResponse(200, "Get product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            throw e;
        }
    }

    @GetMapping(ApiPathConstants.PRODUCT_PRODUCT_SLUG_SLUG)
    ResponseEntity<ApiResponse<?>> getProductById(@PathVariable String slug) {
        try {
            Product product = productService.getProductBySlug(slug);
            return ResponseEntity.ok(new ApiResponse(200, "Get product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            throw e;
        }
    }

    @GetMapping(ApiPathConstants.PRODUCT_PRODUCT_BY_PRODUCT_NAME)
    ResponseEntity<ApiResponse<ProductResponse>> getProductByName(@RequestParam String productName) {
        try {
            Product product = productService.getProductByName(productName);
            return ResponseEntity.ok(new ApiResponse(200, "Get product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            throw e;
        }
    }

    @PostMapping(ApiPathConstants.PRODUCT_INIT_DATA_50)
    ResponseEntity<ApiResponse> initData() {
        productService.initDataProduct();
        return ResponseEntity.ok(new ApiResponse(200, "Init data success!", null));
    }

    @PostMapping(ApiPathConstants.PRODUCT_PRODUCT_ADD)
    ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody AddProductRequest addProductRequest) {
        try {
            Product product = productService.addProduct(addProductRequest);
            return ResponseEntity.status(CREATED).body(new ApiResponse(201, "Add product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            throw e;
        }
    }

    @PutMapping(ApiPathConstants.PRODUCT_PRODUCT_PRODUCT_ID_UPDATE)
    ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long productId, @Valid @RequestBody AddProductRequest addProductRequest) {
        try {
            Product product = productService.updateProduct(addProductRequest, productId);
            return ResponseEntity.status(ACCEPTED).body(new ApiResponse(200, "Update product successfully!", productMapper.toResponse(product)));
        } catch (AppException e) {
            throw e;
        }
    }

    @DeleteMapping(ApiPathConstants.PRODUCT_PRODUCT_PRODUCT_ID_DELETE)
    ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse(200, "Delete product successfully!", productId));
        } catch (AppException e) {
            throw e;
        }
    }
}
