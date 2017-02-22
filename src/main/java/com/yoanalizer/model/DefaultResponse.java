package com.yoanalizer.model;

public class DefaultResponse {
    private String message = "";

    public DefaultResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
