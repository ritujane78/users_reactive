package com.jane.reactive.ws.users.infrastructure;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ErrorResponse> handleValidationErrors(WebExchangeBindException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return Mono.just(ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, message).build());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException ex) {
        return Mono.just(ErrorResponse.builder(ex, HttpStatus.CONFLICT, ex.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ErrorResponse> handleGeneralException(Exception ex) {
        return Mono.just(ErrorResponse.builder(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ErrorResponse> handleBadCredentials(Exception ex) {
        return Mono.just(ErrorResponse.builder(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()).build());
    }
}
