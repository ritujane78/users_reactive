package com.jane.reactive.ws.users.data;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {
    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setUp() {
        UserEntity user1 = new UserEntity(UUID.randomUUID(), "Ritu", "Bafna", "test1@test.com", "123456789");
        UserEntity user2 = new UserEntity(UUID.randomUUID(), "Ritu", "Bafna", "test2@test.com", "123456789");

        String insertSQL = "INSERT INTO users (id, first_name, last_name, email, password) " +
                "VALUES (:id, :first_name, :last_name, :email, :password)";

        Flux.just(user1, user2)
                .concatMap(user -> databaseClient.sql(insertSQL)
                        .bind("id", user.getId())
                        .bind("first_name", user.getFirstName())
                        .bind("last_name", user.getLastName())
                        .bind("email", user.getEmail())
                        .bind("password", user.getPassword())
                        .fetch().rowsUpdated())
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @AfterAll
    void tearDown() {
        databaseClient.sql("TRUNCATE TABLE users")
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    void testFindByEmail_withEmailThatExists_returnsMatchingUsers() {
        // Arrange
        String emailToFind = "test1@test.com";

        // Act and Assert
        StepVerifier.create(userRepository.findByEmail(emailToFind))
                .expectNextMatches(user -> user.getEmail().equals(emailToFind))
                .verifyComplete();

    }
}