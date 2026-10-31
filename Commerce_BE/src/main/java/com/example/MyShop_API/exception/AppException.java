package com.example.MyShop_API.exception;

import com.example.MyShop_API.constants.ErrorCatalogConstants;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private final String messageKey;
    private final Object[] arguments;

    public AppException(String messageKey, Object... arguments) {
        super(messageKey);
        this.messageKey = messageKey;
        this.arguments = arguments == null ? new Object[0] : arguments.clone();
    }

    public Object[] getArguments() {
        return arguments.clone();
    }

    public int getCode() {
        return ErrorCatalogConstants.get(messageKey).code();
    }

    public HttpStatus getHttpStatus() {
        return ErrorCatalogConstants.get(messageKey).status();
    }

    @Override
    public String getMessage() {
        return MessageHandlerUtils.getMessage(messageKey, arguments);
    }
}
