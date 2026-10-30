package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPath {
    public static final String HEALTH = "/actuator/health";
    public static final String HEALTH_ALL = "/actuator/health/**";
    public static final String PREFIX = "${api.prefix}";

    // ===== ADDRESS =====
    public static final String BASE_ADDRESS = PREFIX + "/address";
    public static final String ADDRESS_ALL = "/all";
    public static final String ADDRESS_ADDRESS_ADDRESS_ID = "address/{addressId}";
    public static final String ADDRESS_PROFILE_PROFILE_ID = "/profile/{profileId}";
    public static final String ADDRESS_USER_PROFILE_USER_PROFILE_ID_ADD = "/user-profile/{userProfileId}/add";
    public static final String ADDRESS_UPDATE = "/update";
    public static final String ADDRESS_DELETE = "/delete";

    // ===== AUTH =====
    public static final String BASE_AUTH = PREFIX + "/auth";
    public static final String AUTH_LOGIN = "/login";
    public static final String AUTH_GOOGLE = "/google";
    public static final String AUTH_REFRESH = "/refresh";
    public static final String AUTH_INTROSPECT = "/introspect";
    public static final String AUTH_LOGOUT = "/logout";
    public static final String AUTH_FORGOT_PASSWORD = "/forgot-password";
    public static final String AUTH_RESET_PASSWORD = "/reset-password";

    // ===== CART =====
    public static final String BASE_CART = PREFIX + "/carts";
    public static final String CART_ALL = "/all";
    public static final String CART_PAGE = "/page";
    public static final String CART_CART_CARD_ID = "/cart/{cardId}";
    public static final String CART_CART_USER_PROFILE_PROFILE_ID = "/cart/user-profile/{profileId}";
    public static final String CART_USER_PROFILE_PROFILE_ID_CART_CART_ID_ADD_CART_TO_USER_PROFILE = "/userProfile/{profileId}/cart/{cartId}/addCartToUserProfile";
    public static final String CART_CART_CART_ID_CLEAR = "/cart/{cartId}/clear";
    public static final String CART_CART_CART_ID_TOTAL_PRICE = "/cart/{cartId}/total-price";

    // ===== CART_ITEM =====
    public static final String BASE_CART_ITEM = PREFIX + "/cartItems";
    public static final String CART_ITEM_CART_CART_ID_CART_ITEM_CART_ITEM_ID = "/cart/{cartId}/cartItem/{cartItemId}";
    public static final String CART_ITEM_PROFILE_ID_IDS = "/{profileId}/ids";
    public static final String CART_ITEM_CART_ITEM_ADD_ITEM_TO_CART = "/cartItem/addItemToCart";
    public static final String CART_ITEM_CART_CART_ID_CART_ITEM_CART_ITEM_ID_UPDATE_ITEM_QUANTITY_QUANTITY = "/cart/{cartId}/cartItem/{cartItemId}/updateItemQuantity/{quantity}";
    public static final String CART_ITEM_CART_CART_ID_CART_ITEM_CART_ITEM_ID_REMOVE_ITEM_FROM_CART = "/cart/{cartId}/cartItem/{cartItemId}/removeItemFromCart";

    // ===== CATEGORY =====
    public static final String BASE_CATEGORY = PREFIX + "/categories";
    public static final String CATEGORY_ALL = "/all";
    public static final String CATEGORY_CATEGORY_CATEGORY_ID = "/category/{categoryId}";
    public static final String CATEGORY_CATEGORY_CATEGORY_NAME_NAME = "/category/{categoryName}/name";
    public static final String CATEGORY_CATEGORY_ADD = "/category/add";
    public static final String CATEGORY_CATEGORY_CATEGORY_ID_UPDATE = "/category/{categoryId}/update";
    public static final String CATEGORY_CATEGORY_CATEGORY_ID_DELETE = "/category/{categoryId}/delete";

    // ===== COUPON =====
    public static final String BASE_COUPON = PREFIX + "/coupons";
    public static final String COUPON_ALL = "/all";
    public static final String COUPON_COUPON_AVAILABLE = "/coupon-available";
    public static final String COUPON_GET_AVAILABLE_COUPONS_ORDER_TOTAL = "/get-available-coupons/{orderTotal}";
    public static final String COUPON_COUPON_CREATE = "/coupon/create";
    public static final String COUPON_COUPON_ID = "/{couponId}";

    // ===== IMAGE =====
    public static final String BASE_IMAGE = PREFIX + "/images";
    public static final String IMAGE_UPLOAD_PRODUCT_PRODUCT_ID = "/upload/product/{productId}";
    public static final String IMAGE_PRODUCT_ID_IMAGES_URL = "/{productId}/images/url";
    public static final String IMAGE_IMAGE_DOWNLOAD_IMAGE_ID = "/image/download/{imageId}";
    public static final String IMAGE_PRODUCT_PRODUCT_ID_IMAGES = "/product/{productId}/images";
    public static final String IMAGE_IMAGE_IMAGE_ID_UPDATE = "/image/{imageId}/update";
    public static final String IMAGE_IMAGE_IMAGE_ID_DELETE = "/image/{imageId}/delete";
    public static final String IMAGE_PUT_OBJECT_IMAGE = "/put-object-image";
    public static final String IMAGE_DELETE_OBJECT_IMAGE = "/delete-object-image";
    public static final String IMAGE_GET_OBJECT_IMAGE = "/get-object-image";

    // ===== INVENTORY =====
    public static final String BASE_INVENTORY = PREFIX + "/inventory";
    public static final String INVENTORY_PRODUCT_ID = "/{productId}";
    public static final String INVENTORY_OUT_OF_STOCK = "/out-of-stock";
    public static final String INVENTORY_LOW_STOCK_THRESHOLD = "/low-stock/{threshold}";
    public static final String INVENTORY_CHECK_STOCK = "/checkStock";
    public static final String INVENTORY_UPDATE_STOCK = "/updateStock";
    public static final String INVENTORY_RESERVE = "/reserve";
    public static final String INVENTORY_CANCEL_RESERVATION = "/cancel-reservation";
    public static final String INVENTORY_CONFIRM_ORDER = "/confirm-order";
    public static final String INVENTORY_RESTOCK = "/restock";

    // ===== ORDER =====
    public static final String BASE_ORDER = PREFIX + "/orders";
    public static final String ORDER_BUY_NOW_TEST = "/buy-now-test";
    public static final String ORDER_ALL = "/all";
    public static final String ORDER_ORDER_ORDER_ID = "/order/{orderId}";
    public static final String ORDER_STATUS_PAGE = "/status-page";
    public static final String ORDER_STATUS = "/status";
    public static final String ORDER_PROFILE_PROFILE_ID = "/profile/{profileId}";
    public static final String ORDER_BUY_NOW = "/buy-now";
    public static final String ORDER_FROM_CART_ITEMS = "/from-cart-items";
    public static final String ORDER_PLACE_ORDER = "/placeOrder";
    public static final String ORDER_ORDER_ORDER_ID_CONFIRM_CASH_PAYMENT = "order/{orderId}/confirm-cash-payment";
    public static final String ORDER_ORDER_ORDER_ID_CONFIRM_VNPAY_PAYMENT = "order/{orderId}/confirm-vnpay-payment";
    public static final String ORDER_ORDER_ORDER_ID_ADDRESS_ADDRESS_ID_UPDATE_ORDER_ADDRESS = "order/{orderId}/address/{addressId}/update-order-address";
    public static final String ORDER_ORDER_ORDER_ID_CANCEL_ORDER = "/order/{orderId}/cancelOrder";
    public static final String ORDER_ORDER_UPDATE_STATUS = "/order/updateStatus";
    public static final String ORDER_ORDER_ORDER_ID_DELETE = "/order/{orderId}/delete";

    // ===== ORDER_DELIVERY_ADDRESS =====
    public static final String BASE_ORDER_DELIVERY_ADDRESS = PREFIX + "/delivery-address";
    public static final String ORDER_DELIVERY_ADDRESS_ORDER_ORDER_ID = "/order/{orderId}";

    // ===== ORDER_ITEM =====
    public static final String BASE_ORDER_ITEM = PREFIX + "/orderItem";
    public static final String ORDER_ITEM_ORDER_ID = "/{orderId}";

    // ===== ORDER_STATUS_HISTORY =====
    public static final String BASE_ORDER_STATUS_HISTORY = PREFIX + "/order-status-history";
    public static final String ORDER_STATUS_HISTORY_ORDER_ID = "/{orderId}";

    // ===== PAYMENT =====
    public static final String BASE_PAYMENT = PREFIX + "/payment";
    public static final String PAYMENT_PAYMENT_ID = "/{paymentId}";
    public static final String PAYMENT_ORDER_ORDER_ID = "/order/{orderId}";
    public static final String PAYMENT_VN_PAY = "/vn-pay";
    public static final String PAYMENT_VN_PAY_CALLBACK = "/vn-pay-callback";
    public static final String PAYMENT_COD_ORDER_ID = "/cod/{orderId}";
    public static final String PAYMENT_VNPAY_COMFIRM_ORDER_ID = "/vnpay-comfirm/{orderId}";

    // ===== PRODUCT =====
    public static final String BASE_PRODUCT = PREFIX + "/products";
    public static final String PRODUCT_SEARCH = "/search";
    public static final String PRODUCT_ADMIN_SEARCH = "/admin/search";
    public static final String PRODUCT_CATEGORY_BY_CATEGORY_NAME = "/category/by-category-name";
    public static final String PRODUCT_ALL = "/all";
    public static final String PRODUCT_PAGE = "/page";
    public static final String PRODUCT_PRODUCT_PRODUCT_ID = "/product/{productId}";
    public static final String PRODUCT_PRODUCT_SLUG_SLUG = "/product/{slug}/slug";
    public static final String PRODUCT_PRODUCT_BY_PRODUCT_NAME = "/product/by-product-name";
    public static final String PRODUCT_INIT_DATA_50 = "/init-data-50";
    public static final String PRODUCT_PRODUCT_ADD = "/product/add";
    public static final String PRODUCT_PRODUCT_PRODUCT_ID_UPDATE = "/product/{productId}/update";
    public static final String PRODUCT_PRODUCT_PRODUCT_ID_DELETE = "/product/{productId}/delete";

    // ===== REPORT =====
    public static final String BASE_REPORT = PREFIX + "/report";
    public static final String REPORT_REPORT_ORDER_PDF_ORDER_ID = "/report-order-pdf/{orderId}";
    public static final String REPORT_EXPORT_ORDERS = "/export/orders";
    public static final String REPORT_MONTHLY_REVENUE = "/monthly-revenue";
    public static final String REPORT_QUARTERLY_REVENUE = "/quarterly-revenue";
    public static final String REPORT_ANNUAL_REVENUE = "/annual-revenue";
    public static final String REPORT_PRODUCT_REVENUE = "/product-revenue";
    public static final String REPORT_CATEGORY_REVENUE = "/category-revenue";

    // ===== REVIEW =====
    public static final String BASE_REVIEW = PREFIX + "/reviews";
    public static final String REVIEW_PRODUCT_PRODUCT_ID = "/product/{productId}";
    public static final String REVIEW_CREATE_REVIEW = "/create-review";

    // ===== ROLE =====
    public static final String BASE_ROLE = PREFIX + "/admin/roles";
    public static final String ROLE_ALL = "/all";
    public static final String ROLE_ID = "/{id}";
    public static final String ROLE_ADD = "/add";
    public static final String ROLE_ID_UPDATE = "/{id}/update";
    public static final String ROLE_ID_DELETE = "/{id}/delete";

    // ===== USER =====
    public static final String BASE_USER = PREFIX + "/users";
    public static final String USER_ALL = "/all";
    public static final String USER_ID = "/{id}";
    public static final String USER_REGISTRATION = "/registration";
    public static final String USER_ID_UPDATE_ROLE_USER = "/{id}/update-role-user";
    public static final String USER_USER_ID_LOCK_USER = "/{userId}/lock-user";
    public static final String USER_USER_ID_UNLOCKED_USER = "/{userId}/unlocked-user";
    public static final String USER_USER_ID_CHANGE_PASSWORD = "/{userId}/change-password";
    public static final String USER_ID_DELETE = "/{id}/delete";
    public static final String USER_MY_INFO = "/myInfo";

    // ===== USER_PROFILE =====
    public static final String BASE_USER_PROFILE = PREFIX + "/profiles";
    public static final String USER_PROFILE_ALL = "/all";
    public static final String USER_PROFILE_PROFILE_ACCOUNT_ID = "/profile/{account_id}";
    public static final String USER_PROFILE_USER_USER_ID_CREATE_OR_UPDATE_PROFILE = "/user/{userId}/createOrUpdateProfile";

    // ===== SECURITY MATCHERS =====
    public static final String PUBLIC_AUTH_ALL = BASE_AUTH + "/**";
    public static final String PUBLIC_AUTH_GOOGLE = BASE_AUTH + AUTH_GOOGLE;
    public static final String PUBLIC_USERS_REGISTRATION = BASE_USER + USER_REGISTRATION;
    public static final String PUBLIC_AUTH_FORGOT_PASSWORD = BASE_AUTH + AUTH_FORGOT_PASSWORD;
    public static final String PUBLIC_AUTH_RESET_PASSWORD = BASE_AUTH + AUTH_RESET_PASSWORD;
    public static final String PUBLIC_CATEGORIES_ALL = BASE_CATEGORY + "/**";
    public static final String PUBLIC_PRODUCTS_PRODUCT_BY_PRODUCT_NAME = BASE_PRODUCT + PRODUCT_PRODUCT_BY_PRODUCT_NAME;
    public static final String PUBLIC_PRODUCTS_PAGE = BASE_PRODUCT + PRODUCT_PAGE;
    public static final String PUBLIC_PRODUCTS_CATEGORY_BY_CATEGORY_NAME = BASE_PRODUCT + PRODUCT_CATEGORY_BY_CATEGORY_NAME;
    public static final String PUBLIC_PRODUCTS_PRODUCT_PRODUCT_ID = BASE_PRODUCT + PRODUCT_PRODUCT_PRODUCT_ID;
    public static final String PUBLIC_PRODUCTS_PRODUCT_SLUG_SLUG = BASE_PRODUCT + PRODUCT_PRODUCT_SLUG_SLUG;
    public static final String PUBLIC_PRODUCTS_ALL = BASE_PRODUCT + PRODUCT_ALL;
    public static final String PUBLIC_PAYMENT_VN_PAY_CALLBACK = BASE_PAYMENT + PAYMENT_VN_PAY_CALLBACK;
    public static final String PUBLIC_IMAGES_ALL = BASE_IMAGE + "/**";
    public static final String PUBLIC_REVIEWS_PRODUCT_PRODUCT_ID = BASE_REVIEW + REVIEW_PRODUCT_PRODUCT_ID;
    public static final String PUBLIC_OAUTH2_ALL = "/oauth2/**";
    public static final String PUBLIC_LOGIN_OAUTH2_ALL = "/login/oauth2/**";
    public static final String PUBLIC_SWAGGER_UI_ALL = "/swagger-ui/**";
    public static final String PUBLIC_V3_API_DOCS_ALL = "/v3/api-docs/**";
    public static final String PUBLIC_SWAGGER_RESOURCES_ALL = "/swagger-resources/**";
    public static final String PUBLIC_WEBJARS_ALL = "/webjars/**";

    public static String resolve(String prefix, String path) {
        return path.replace(PREFIX, prefix);
    }
}
