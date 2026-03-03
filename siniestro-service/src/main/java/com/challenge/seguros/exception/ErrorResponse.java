package com.challenge.seguros.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String mensaje,
        int status,
        LocalDateTime timestamp
) {
    public ErrorResponse(String mensaje, int status) {
        this(mensaje, status, LocalDateTime.now());
    }
}
