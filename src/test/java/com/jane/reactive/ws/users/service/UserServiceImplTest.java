package com.jane.reactive.ws.users.service;

import com.jane.reactive.ws.users.data.UserEntity;
import com.jane.reactive.ws.users.data.UserRepository;
import com.jane.reactive.ws.users.presentation.model.CreateUserRequest;
import com.jane.reactive.ws.users.presentation.model.UserRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WebClient webClient;

    private Sinks.Many<UserRest> usersSink;
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        usersSink = Sinks.many().multicast().onBackpressureBuffer();
        userService = new UserServiceImpl(userRepository, passwordEncoder, usersSink, webClient);
    }
    @Test
    void testCreateUser_withValidRequest_returnsUsrDetails() {
        // Arrange
        CreateUserRequest createUserRequest = new CreateUserRequest(
                "Ritu",
                "Bafna",
                "ritubafna@example.xom",
                "123456789"
        );
        UserEntity savedEntity = new UserEntity(
                UUID.randomUUID(),
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getEmail(),
                createUserRequest.getPassword()
        );
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(savedEntity));

        // Act
        Mono<UserRest> result = userService.createUser(Mono.just(createUserRequest));

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(userRest -> userRest.getId().equals(savedEntity.getId()) &&
                        userRest.getFirstName().equals(createUserRequest.getFirstName()) &&
                        userRest.getLastName().equals(createUserRequest.getLastName()) &&
                        userRest.getEmail().equals(createUserRequest.getEmail()))
                .verifyComplete();

//        UserRest user = result.block();
//        assertEquals(savedEntity.getId(), user.getId());
//        assertEquals(savedEntity.getFirstName(), user.getFirstName());
    }
}