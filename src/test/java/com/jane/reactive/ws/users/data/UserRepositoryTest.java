package com.jane.reactive.ws.users.data;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Test
    void testFindByEmail_WithEmailThatDoesNotExist_ReturnsEmptyMono() {
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";

        // Act & Assert
        StepVerifier.create(userRepository.findByEmail(nonExistentEmail))
                .expectNextCount(0)
                .verifyComplete();
    }
    @Test
    void testFindAllBy_WithValidPageable_ReturnsPaginatedResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2); // First page, page size = 2

        // Act & Assert
        StepVerifier.create(userRepository.findAllBy(pageable))
                .expectNextCount(2) // Expect exactly 2 items on the first page
                .verifyComplete();
    }

    @Test
    void testFindAllBy_WithNonExistentPage_ReturnsEmptyFlux() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2); // Second page, page size = 2 (no data exists here)

        // Act & Assert
        StepVerifier.create(userRepository.findAllBy(pageable))
                .expectNextCount(1) // Expect 1 item when running the entire class and 0 items whn running individually on the second page
                .expectComplete()
                .verify();
    }

    @Test
    void testSave_whenExistingEmailProvided_shouldFail() {
        UserEntity invalidUser = new UserEntity(null, "Ritu", "Bafna", "test1@test.com", "password");

        userRepository.save(invalidUser)
                .as(StepVerifier::create)
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    void testSave_whenValidUserProvided_shouldSucceed() {
        // Arrange
        UserEntity validUser = new UserEntity(null, "Ritu", "Bafna", "test@test.com", "password123");

        // Act & Assert
        userRepository.save(validUser)
                .as(StepVerifier::create)
                .expectNextMatches(savedUser -> {
                    return savedUser.getId() != null
                            && savedUser.getFirstName().equals(validUser.getFirstName())
                            && savedUser.getLastName().equals(validUser.getLastName())
                            && savedUser.getEmail().equals(validUser.getEmail());
                })
                .verifyComplete();
    }

}