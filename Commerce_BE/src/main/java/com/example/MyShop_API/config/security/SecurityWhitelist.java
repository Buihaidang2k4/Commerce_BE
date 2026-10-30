package com.example.MyShop_API.config.security;

import java.util.List;
import com.example.MyShop_API.constants.ApiPath;

public final class SecurityWhitelist {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            // Auth
            ApiPath.PUBLIC_AUTH_ALL,
            ApiPath.PUBLIC_AUTH_GOOGLE,
            ApiPath.PUBLIC_USERS_REGISTRATION,
            ApiPath.PUBLIC_AUTH_FORGOT_PASSWORD,
            ApiPath.PUBLIC_AUTH_RESET_PASSWORD,

            // categories
            ApiPath.PUBLIC_CATEGORIES_ALL,

            // product
            ApiPath.PUBLIC_PRODUCTS_PRODUCT_BY_PRODUCT_NAME,
            ApiPath.PUBLIC_PRODUCTS_PAGE,
            ApiPath.PUBLIC_PRODUCTS_CATEGORY_BY_CATEGORY_NAME,
            ApiPath.PUBLIC_PRODUCTS_PRODUCT_PRODUCT_ID,
            ApiPath.PUBLIC_PRODUCTS_PRODUCT_SLUG_SLUG,
            ApiPath.PUBLIC_PRODUCTS_ALL,

            // payment
            ApiPath.PUBLIC_PAYMENT_VN_PAY_CALLBACK,

            // image
            ApiPath.PUBLIC_IMAGES_ALL,

            // review
            ApiPath.PUBLIC_REVIEWS_PRODUCT_PRODUCT_ID,
            ApiPath.PUBLIC_OAUTH2_ALL,
            ApiPath.PUBLIC_LOGIN_OAUTH2_ALL
    );
    // swagger
    public static final List<String> SWAGGER_ENDPOINTS = List.of(
            ApiPath.PUBLIC_SWAGGER_UI_ALL, ApiPath.PUBLIC_V3_API_DOCS_ALL,
            ApiPath.PUBLIC_SWAGGER_RESOURCES_ALL, ApiPath.PUBLIC_WEBJARS_ALL
    );


    private SecurityWhitelist() {
    }
}
