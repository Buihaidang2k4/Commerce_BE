package com.example.MyShop_API.exception;

import com.example.MyShop_API.constants.ErrorCatalogConstants;

import com.example.MyShop_API.constants.CommonMessageConstants;
import com.example.MyShop_API.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException exception) {
        String message = exception.getMessage();
        log.warn("[{}] {}", exception.getCode(), message);
        return ResponseEntity.status(exception.getHttpStatus())
                .body(ApiResponse.error(exception.getCode(), message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidValue(IllegalArgumentException exception) {
        return error(CommonMessageConstants.VALIDATION_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception exception) {
        String message = MessageHandlerUtils.getMessage(CommonMessageConstants.UNCATEGORIZED_EXCEPTION);
        log.error(message, exception);
        return error(CommonMessageConstants.UNCATEGORIZED_EXCEPTION);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
        return error(CommonMessageConstants.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn(message);
        return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCatalogConstants.get(CommonMessageConstants.VALIDATION_ERROR).code(), message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonParseError(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCatalogConstants.get(CommonMessageConstants.VALIDATION_ERROR).code(),
                MessageHandlerUtils.getMessage(CommonMessageConstants.MALFORMED_JSON)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParam(MissingServletRequestParameterException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCatalogConstants.get(CommonMessageConstants.VALIDATION_ERROR).code(),
                MessageHandlerUtils.getMessage(CommonMessageConstants.MISSING_PARAMETER, exception.getParameterName())));
    }

    private ResponseEntity<ApiResponse<?>> error(String key) {
        var definition = ErrorCatalogConstants.get(key);
        String message = MessageHandlerUtils.getMessage(key);
        log.warn(message);
        return ResponseEntity.status(definition.status()).body(ApiResponse.error(definition.code(), message));
    }
}
