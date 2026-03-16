package com.jane.reactive.ws.users.service;

import com.jane.reactive.ws.users.presentation.model.CreateUserRequest;
import com.jane.reactive.ws.users.presentation.model.UserRest;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface UserService extends ReactiveUserDetailsService {
    Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequest);
    Mono<UserRest> getUserById(UUID id, String include, String jwt);
    Flux<UserRest> getAllUsers(int page, int limit);
    Flux<UserRest> streamUser();
}
