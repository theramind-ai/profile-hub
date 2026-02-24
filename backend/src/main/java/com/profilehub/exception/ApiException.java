package com.profilehub.exception;

public class ApiException extends RuntimeException {

    private int statusCode;
    private String message;

    public ApiException(String message) {
        super(message);
        this.message = message;
        this.statusCode = 400;
    }

    public ApiException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.statusCode = 400;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
