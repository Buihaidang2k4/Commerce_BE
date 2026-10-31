package com.example.MyShop_API.constants;

import java.util.List;

public final class SecurityWhitelistConstants {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            // Auth
            ApiPathConstants.BASE_AUTH + ApiPathConstants.AUTH_LOGIN,
            ApiPathConstants.BASE_AUTH + ApiPathConstants.AUTH_REFRESH,
            ApiPathConstants.BASE_AUTH + ApiPathConstants.AUTH_INTROSPECT,
            ApiPathConstants.BASE_AUTH + ApiPathConstants.AUTH_LOGOUT,
            ApiPathConstants.PUBLIC_USERS_REGISTRATION,
            ApiPathConstants.PUBLIC_AUTH_FORGOT_PASSWORD,
            ApiPathConstants.PUBLIC_AUTH_RESET_PASSWORD,

            // categories
            ApiPathConstants.PUBLIC_CATEGORIES_ALL,

            // product
            ApiPathConstants.PUBLIC_PRODUCTS_PRODUCT_BY_PRODUCT_NAME,
            ApiPathConstants.PUBLIC_PRODUCTS_PAGE,
            ApiPathConstants.PUBLIC_PRODUCTS_CATEGORY_BY_CATEGORY_NAME,
            ApiPathConstants.PUBLIC_PRODUCTS_PRODUCT_PRODUCT_ID,
            ApiPathConstants.PUBLIC_PRODUCTS_PRODUCT_SLUG_SLUG,
            ApiPathConstants.PUBLIC_PRODUCTS_ALL,

            // payment
            ApiPathConstants.PUBLIC_PAYMENT_VN_PAY_CALLBACK,

            // image
            ApiPathConstants.PUBLIC_IMAGES_ALL,

            // review
            ApiPathConstants.PUBLIC_REVIEWS_PRODUCT_PRODUCT_ID
    );
    // swagger
    public static final List<String> SWAGGER_ENDPOINTS = List.of(
            ApiPathConstants.PUBLIC_SWAGGER_UI_ALL, ApiPathConstants.PUBLIC_V3_API_DOCS_ALL,
            ApiPathConstants.PUBLIC_SWAGGER_RESOURCES_ALL, ApiPathConstants.PUBLIC_WEBJARS_ALL
    );


    private SecurityWhitelistConstants() {
    }
}
