package com.yoanalizer.exception;

public class InvalidTokenException extends RuntimeException {
    private static final long serialVersionUID = 4785618439794756800L;

    public InvalidTokenException(String token){
        super("Invalid token: " + token);
    }
}
