package com.example.MyShop_API.exception;

import com.example.MyShop_API.constants.CommonMessageUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

class MessageHandlerUtilsTest {
    @AfterEach void clearRequest() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test void defaultsToVietnameseWithoutRequest() {
        assertEquals("Không tìm thấy danh mục", MessageHandlerUtils.getMessage(CommonMessageUtils.CATEGORY_NOT_EXISTED));
    }

    @Test void readsCaseInsensitiveLocaleHeaderAndRegion() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Locale", "en-US");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        AppException exception = new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED);
        assertEquals("Category not found", exception.getMessage());
        assertEquals(1500, exception.getCode());
        assertEquals(404, exception.getHttpStatus().value());
    }

    @Test void unsupportedLocaleFallsBackToVietnamese() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("locale", "fr-FR");
        assertEquals(Locale.forLanguageTag("vi"), MessageHandlerUtils.resolveLocale(request));
    }

    @Test void interpolatesArgumentsInBothLanguages() {
        assertEquals("Requested: 3, Available: 1", MessageHandlerUtils.getMessage(
                CommonMessageUtils.PRODUCT_NOT_ENOUGH, Locale.ENGLISH, 3, 1));
        assertEquals("Số lượng yêu cầu: 3, số lượng còn lại: 1", MessageHandlerUtils.getMessage(
                CommonMessageUtils.PRODUCT_NOT_ENOUGH, Locale.forLanguageTag("vi"), 3, 1));
    }

    @Test void unknownKeyHasPredictableFallback() {
        assertEquals("missing.key", MessageHandlerUtils.getMessage("missing.key"));
    }
}
