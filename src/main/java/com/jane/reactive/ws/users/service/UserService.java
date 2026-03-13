package com.jane.reactive.ws.users.service;

import com.jane.reactive.ws.users.presentation.CreateUserRequest;
import com.jane.reactive.ws.users.presentation.UserRest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface UserService {
    Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequest);
    Mono<UserRest> getUserById(UUID id);
    Flux<UserRest> getAllUsers(int page, int limit);
}
