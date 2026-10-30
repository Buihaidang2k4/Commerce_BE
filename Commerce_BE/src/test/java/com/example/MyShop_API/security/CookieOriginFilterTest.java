package com.example.MyShop_API.security;

import com.example.MyShop_API.config.security.CookieOriginFilter;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class CookieOriginFilterTest {
    private final CookieOriginFilter filter = new CookieOriginFilter(List.of("https://shop.example"));

    private boolean run(String method, String origin, String referer, boolean cookie) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(method, "/api/v1/orders");
        if (origin != null) request.addHeader("Origin", origin);
        if (referer != null) request.addHeader("Referer", referer);
        if (cookie) request.setCookies(new Cookie("access_token", "test"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean passed = new AtomicBoolean();
        filter.doFilter(request, response, (req, res) -> passed.set(true));
        if (!passed.get()) assertEquals(403, response.getStatus());
        return passed.get();
    }

    @Test void acceptsAllowedOrigin() throws Exception {
        assertTrue(run("POST", "https://shop.example", null, true));
    }
    @Test void rejectsForeignOriginEvenWithAllowedReferer() throws Exception {
        assertFalse(run("POST", "https://evil.example", "https://shop.example/cart", true));
    }
    @Test void requiresOriginForCookieWrites() throws Exception {
        assertFalse(run("DELETE", null, null, true));
    }
    @Test void acceptsAllowedRefererFallback() throws Exception {
        assertTrue(run("PATCH", null, "https://shop.example/cart", true));
    }
    @Test void rejectsLookalikeReferer() throws Exception {
        assertFalse(run("POST", null, "https://shop.example.evil.test/cart", true));
    }
    @Test void permitsSafeRequestsAndNonBrowserClients() throws Exception {
        assertTrue(run("GET", null, null, true));
        assertTrue(run("POST", null, null, false));
    }
}
