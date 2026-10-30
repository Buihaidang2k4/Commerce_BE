package com.example.MyShop_API.security;

import com.example.MyShop_API.config.security.AuthRateLimitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthRateLimitFilterTest {
    @Test void rejectsRequestsAboveLimit() throws Exception {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        when(redis.execute(any(RedisScript.class), anyList(), any(Object[].class))).thenReturn(21L);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setServletPath("/api/v1/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean passed = new AtomicBoolean();
        new AuthRateLimitFilter(redis, "/api/v1", 20, 60)
                .doFilter(request, response, (req, res) -> passed.set(true));
        assertFalse(passed.get());
        assertEquals(429, response.getStatus());
        assertEquals("60", response.getHeader("Retry-After"));
    }

    @Test void failsClosedWhenRedisIsUnavailable() throws Exception {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        when(redis.execute(any(RedisScript.class), anyList(), any(Object[].class)))
                .thenThrow(new RedisConnectionFailureException("offline"));
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/forgot-password");
        request.setServletPath("/api/v1/auth/forgot-password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new AuthRateLimitFilter(redis, "/api/v1", 20, 60)
                .doFilter(request, response, (req, res) -> fail("Must not bypass rate limits"));
        assertEquals(503, response.getStatus());
    }
}
