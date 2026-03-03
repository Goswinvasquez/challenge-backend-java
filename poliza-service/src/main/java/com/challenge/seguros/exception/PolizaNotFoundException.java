package com.challenge.seguros.exception;

public class PolizaNotFoundException extends RuntimeException {
    public PolizaNotFoundException(String message) {
        super(message);
    }
}