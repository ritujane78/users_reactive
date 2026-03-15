package com.jane.reactive.ws.users.service;

import reactor.core.publisher.Mono;

public interface JwtService {
    String generateJwt(String subject);
    Mono<Boolean> validateJwt(String jwt);
    String extractTokenSubject(String token);
}
