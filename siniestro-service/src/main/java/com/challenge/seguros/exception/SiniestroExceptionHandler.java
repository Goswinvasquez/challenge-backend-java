package com.challenge.seguros.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class SiniestroExceptionHandler {

    @ExceptionHandler(PolizaInvalidaException.class)
    public Mono<ResponseEntity<String>> handlePolizaInvalida(PolizaInvalidaException ex) {
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }
}