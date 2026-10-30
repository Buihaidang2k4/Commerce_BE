package com.example.MyShop_API.exception;

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
        return ErrorCatalog.get(messageKey).code();
    }

    public HttpStatus getHttpStatus() {
        return ErrorCatalog.get(messageKey).status();
    }

    @Override
    public String getMessage() {
        return MessageHandlerUtils.getMessage(messageKey, arguments);
    }
}
