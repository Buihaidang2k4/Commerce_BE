package com.example.MyShop_API.service.product;

import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.dto.request.productSearch.AdminProductSearchCondition;
import com.example.MyShop_API.dto.request.productSearch.UserProductSearchCondition;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Inventory;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.mapper.ProductMapper;
import com.example.MyShop_API.repo.*;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.utils.SlugUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    CartItemRepository cartItemRepository;
    IInventoryService inventoryService;
    OrderItemRepository orderItemRepository;

    @Transactional
    public Product addProduct(AddProductRequest request) {
        log.info("=================== START ADD PRODUCT ====================");
        // Tìm danh mục
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED));

        if (productRepository.existsProductByProductName(request.getProductName()))
            throw new AppException(CommonMessageUtils.PRODUCT_NAME_IS_EXISTED);

        String slug = SlugUtils.toSlug(request.getProductName());

        // mapper
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setSpecialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()));
        product.setSlug(slug);
        // Lưu khởi tạo id
        product = productRepository.save(product);

        // Khởi tạo tồn kho cho product
        inventoryService.initializeInventory(product, request.getQuantity());
        log.info("=================== END ADD PRODUCT ====================");
        return product;
    }

    @Transactional
    public Product updateProduct(AddProductRequest request, Long productId) {
        log.info("=================== START UPDATE PRODUCT ===================");
        Product findProduct = productRepository.findById(productId).orElseThrow(
                () -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));

        // Check category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED));
        if (category == null)
            throw new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED);
        findProduct.setCategory(category);
        findProduct.setUpdateAt(LocalDate.now());
        productMapper.update(request, findProduct);
        // Cập nhật tồn kho
        inventoryService.updateStock(productId, request.getQuantity());

        // Tính lại giá sản phẩm đặc biệt nếu có mã giảm giá
        findProduct.setSpecialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()));
        log.info("=================== END UPDATE PRODUCT ===================");
        return productRepository.save(findProduct);
    }

    @Transactional
    public void deleteProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED)
        );

        if (orderItemRepository.existsByProductProductId(product.getProductId())) {
            throw new AppException(CommonMessageUtils.PRODUCT_HAS_ORDERS);
        }

        cartItemRepository.deleteCartItemByProductProductId(productId);
        productRepository.deleteById(productId);
    }

    @Override
    @Transactional
    public void initDataProduct() {
        List<Product> products = new ArrayList<>();

        // Tạo sẵn vài Category
        Category electronics = Category.builder()
                .categoryName("Cây ăn quả")
                .description("Các loại cây ăn quả")
                .build();

        Category fashion = Category.builder()
                .categoryName("Cây lấy gỗ")
                .description("Các loại cây lấy gỗ")
                .build();

        Category home = Category.builder()
                .categoryName("Rau củ quả")
                .description("Các loại rau củ quả")
                .build();

        categoryRepository.saveAll(List.of(electronics, fashion, home));

        for (int i = 0; i < 50; i++) {
            // Chọn category luân phiên
            Category category = (i % 3 == 0) ? electronics : (i % 3 == 1 ? fashion : home);

            Product product = Product.builder()
                    .productName("Sản phẩm " + i)
                    .origin("Việt Nam " + i)
                    .bio("Sản phẩm chất lượng " + i)
                    .slug("san-pham-" + i)
                    .height(20.0)
                    .length(10.0)
                    .weight(50.0)
                    .width(100.0)
                    .description("Mô tả sản phẩm " + i)
                    .category(category)
                    .price(BigDecimal.valueOf(1000 * i))
                    .specialPrice(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .soldCount(0)
                    .reviewCount(0)
                    .avgRating(0.0)
                    .createAt(LocalDate.now())
                    .build();

            // Tạo tồn kho cho sản phẩm
            Inventory inventory = Inventory.builder()
                    .available(100 + i)
                    .product(product)
                    .build();

            product.setInventory(inventory);

            products.add(product);
        }

        productRepository.saveAll(products); // cascade sẽ lưu cả inventory
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProductsForUser(UserProductSearchCondition condition, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecification.hasKeyword(condition.getKeyword()))
                .and(ProductSpecification.hasCategoryName(condition.getCategoryName()))
                .and(ProductSpecification.priceBetween(condition.getMinPrice(), condition.getMaxPrice()))
                .and(ProductSpecification.hasDiscount(condition.getHasDiscount()))
                .and(ProductSpecification.minRating(condition.getRating()))
                .and(ProductSpecification.origin(condition.getOrigin()))
                .and(ProductSpecification.bestSeller(condition.getBestSeller()));
        return productRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProductsForAdmin(AdminProductSearchCondition condition, Pageable pageable) {
        Specification<Product> spec = Specification
                .where(ProductSpecification.hasKeyword(condition.getKeyword()))
                .and(ProductSpecification.hasCategoryName(condition.getCategoryName()))
                .and(ProductSpecification.priceBetween(condition.getMinPrice(), condition.getMaxPrice()))
                .and(ProductSpecification.hasDiscount(condition.getHasDiscount()))
                .and(ProductSpecification.bestSeller(condition.getBestSeller()))
                .and(ProductSpecification.inStock(condition.getInStock()))
                .and(ProductSpecification.createdBetween(condition.getFromDate(), condition.getToDate()));
        return productRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCouponIds(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProducts() {
        log.info("=======getProducts======== ");
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductBySlug(String slug) {
        return productRepository.findBySlug(slug).orElseThrow(() -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));
    }

    // ============== GET ALL ===================
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));
    }

    // ============ GET ALL BY PRODUCT NAME =============
    @Override
    @Transactional(readOnly = true)
    public Product getProductByName(String productName) {
        return productRepository.getProductByProductName(productName).orElseThrow(() -> new AppException(CommonMessageUtils.PRODUCT_NOT_EXISTED));
    }

    // ============== GET ALL BY CATEGORY NAME ============
    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProductByCategory(String categoryName, Pageable pageable) {
        Category findCategory = categoryRepository.getCategoriesByCategoryName(categoryName);
        if (findCategory == null)
            throw new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED);
        return productRepository.findByCategory_CategoryName(categoryName, pageable);
    }

    private BigDecimal calculateSpecialPrice(BigDecimal price, BigDecimal discount) {
        return price.multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100))));
    }
}
