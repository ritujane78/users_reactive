package com.jane.reactive.ws.users.service;

import com.jane.reactive.ws.users.data.UserEntity;
import com.jane.reactive.ws.users.data.UserRepository;
import com.jane.reactive.ws.users.presentation.model.CreateUserRequest;
import com.jane.reactive.ws.users.presentation.model.UserRest;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;


import java.util.ArrayList;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Sinks.Many<UserRest> userSink;

    @Override
    public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequest) {
        return createUserRequest
                .flatMap(this::convertToUserEntity)
                .flatMap(userRepository::save)
                .map(this::convertToUserRest)
                .doOnSuccess(savedUser -> userSink.tryEmitNext(savedUser) );

//        // not used because exceptions handled globally
//                .onErrorMap(exception -> {
//                    if(exception instanceof DuplicateKeyException){
//                        return new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
//                    }else if(exception instanceof DataIntegrityViolationException){
//                        return new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
//                    }else {
//                        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
//                    }
//                } )
    }

    @Override
    public Mono<UserRest> getUserById(UUID id) {
        Mono<UserEntity> userEntityMono = userRepository.findById(id);
        return userEntityMono
                .mapNotNull(this::convertToUserRest);
    }

    @Override
    public Flux<UserRest> getAllUsers(int page, int limit) {
        if(page > 0)page--;
        Pageable pageable = PageRequest.of(page, limit);
        return userRepository.findAllBy(pageable)
                .map(userEntity -> convertToUserRest(userEntity));
    }

    @Override
    public Flux<UserRest> streamUser() {
        return userSink.asFlux()
                .publish()
                .autoConnect(1);
    }

    private Mono<UserEntity> convertToUserEntity(CreateUserRequest createUserRequest) {
        return Mono.fromCallable(() -> {
            UserEntity user = new UserEntity();
            BeanUtils.copyProperties(createUserRequest, user);
            user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            return user;
        }).subscribeOn(Schedulers.boundedElastic());

    }

    private UserRest convertToUserRest(UserEntity userEntity) {
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userEntity, userRest);
        return userRest;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(userEntity ->  User
                                .withUsername(userEntity.getEmail())
                                .password(userEntity.getPassword())
                                .authorities(new ArrayList<>())
                                .build()
                );
    }
}
