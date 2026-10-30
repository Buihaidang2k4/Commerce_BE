package com.example.MyShop_API.exception;

import com.example.MyShop_API.config.MessageConfig;
import com.example.MyShop_API.constants.CommonMessageUtils;
import com.example.MyShop_API.config.security.JwtAuthenticationEntryPoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LocalizedErrorResponseTest {
    @RestController
    static class FixtureController {
        @GetMapping("/test/category")
        void category() { throw new AppException(CommonMessageUtils.CATEGORY_NOT_EXISTED); }

        @PostMapping("/test/validate")
        void validate(@Valid @RequestBody Input input) {}
    }

    record Input(@NotBlank(message = "{validation.email.is.required}") String email) {}

    @Test void localizesDomainAndValidationErrorsThroughMvc() throws Exception {
        try (LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean()) {
            validator.setValidationMessageSource(MessageHandlerUtils.getMessageSource());
            validator.afterPropertiesSet();
            var mvc = MockMvcBuilders.standaloneSetup(new FixtureController())
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .setLocaleResolver(new MessageConfig().localeResolver())
                    .setValidator(validator).build();
            mvc.perform(get("/test/category").accept(MediaType.APPLICATION_JSON).header("locale", "en"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(1500))
                    .andExpect(jsonPath("$.message").value("Category not found"));
            mvc.perform(get("/test/category").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Không tìm thấy danh mục"));
            mvc.perform(post("/test/validate").accept(MediaType.APPLICATION_JSON).header("locale", "en")
                            .contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("email: Email is required"));
        }
    }

    @Test void securityErrorsHonorLocaleWithoutMvcRequestContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("locale", "en");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new JwtAuthenticationEntryPoint().commence(request, response,
                new InsufficientAuthenticationException("test"));
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Authentication required"));
        assertEquals("UTF-8", response.getCharacterEncoding());
    }
}
