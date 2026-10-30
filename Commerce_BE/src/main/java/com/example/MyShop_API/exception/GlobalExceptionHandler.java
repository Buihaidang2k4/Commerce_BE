package com.example.MyShop_API.exception;

import com.example.MyShop_API.constants.CommonMessageUtils;
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception exception) {
        String message = MessageHandlerUtils.getMessage(CommonMessageUtils.UNCATEGORIZED_EXCEPTION);
        log.error(message, exception);
        return error(CommonMessageUtils.UNCATEGORIZED_EXCEPTION);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException exception) {
        return error(CommonMessageUtils.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn(message);
        return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCatalog.get(CommonMessageUtils.VALIDATION_ERROR).code(), message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleJsonParseError(HttpMessageNotReadableException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCatalog.get(CommonMessageUtils.VALIDATION_ERROR).code(),
                MessageHandlerUtils.getMessage(CommonMessageUtils.MALFORMED_JSON)));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParam(MissingServletRequestParameterException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.error(
                ErrorCatalog.get(CommonMessageUtils.VALIDATION_ERROR).code(),
                MessageHandlerUtils.getMessage(CommonMessageUtils.MISSING_PARAMETER, exception.getParameterName())));
    }

    private ResponseEntity<ApiResponse<?>> error(String key) {
        var definition = ErrorCatalog.get(key);
        String message = MessageHandlerUtils.getMessage(key);
        log.warn(message);
        return ResponseEntity.status(definition.status()).body(ApiResponse.error(definition.code(), message));
    }
}
