package com.example.MyShop_API.service.product;

import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.dto.request.productSearch.AdminProductSearchCondition;
import com.example.MyShop_API.dto.request.productSearch.UserProductSearchCondition;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    Page<Product> searchProductsForUser(UserProductSearchCondition condition, Pageable pageable);

    Page<Product> searchProductsForAdmin(AdminProductSearchCondition condition, Pageable pageable);

    List<ProductResponse> getProductsByCouponIds(List<Long> productIds);

    List<Product> getProducts();

    Product getProductBySlug(String slug);

    Product getProductById(Long id);

    Product getProductByName(String productName);

    Page<Product> getProducts(Pageable pageable);

    Page<Product> searchProductByCategory(String categoryName, Pageable pageable);

    Product addProduct(AddProductRequest addProductRequest);

    Product updateProduct(AddProductRequest addProductRequest, Long productId);

    void deleteProductById(Long productId);

    // Lấy  doanh thu của sản phẩm theo khoảng thời gian
    void initDataProduct();


}
