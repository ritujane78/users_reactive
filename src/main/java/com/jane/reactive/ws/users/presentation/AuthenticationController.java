package com.jane.reactive.ws.users.presentation;

import com.jane.reactive.ws.users.presentation.model.AuthenticationRequest;
import com.jane.reactive.ws.users.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public Mono<ResponseEntity<Object>> login(@RequestBody Mono<AuthenticationRequest> authenticationRequestMono) {
        return authenticationRequestMono
                .flatMap(authenticationRequest -> authenticationService
                        .authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword()))
                .map(authenticationResponseMap -> ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + authenticationResponseMap.get("token"))
                        .header("UserId", authenticationResponseMap.get("userId"))
                        .build());

        // handle exception globally
//                .onErrorReturn(BadCredentialsException.class, ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body("Invalid Credentials"))
//                .onErrorReturn(Exception.class, ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .build());
    }
}
