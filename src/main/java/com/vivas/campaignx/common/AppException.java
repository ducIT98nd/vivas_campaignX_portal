package com.vivas.campaignx.common;

public class AppException extends Exception {
    private String message;

    public AppException(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
