package com.jane.reactive.ws.users.service;

import com.jane.reactive.ws.users.data.UserEntity;
import com.jane.reactive.ws.users.data.UserRepository;
import com.jane.reactive.ws.users.presentation.model.AlbumRest;
import com.jane.reactive.ws.users.presentation.model.CreateUserRequest;
import com.jane.reactive.ws.users.presentation.model.UserRest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;


import java.util.ArrayList;
import java.util.UUID;
import java.util.WeakHashMap;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Sinks.Many<UserRest> userSink;
    private final WebClient webClient;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, Sinks.Many<UserRest> userSink, WebClient webClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSink = userSink;
        this.webClient = webClient;
    }

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
    public Mono<UserRest> getUserById(UUID id, String include,  String jwt) {
        Mono<UserEntity> userEntityMono = userRepository.findById(id);
        return userEntityMono
                .mapNotNull(this::convertToUserRest)
                .flatMap(user -> {
                    if(include !=null && include.equals("albums")){
                        // fetch album
                        return includeUserAlbums(user, jwt);
                    }
                    return Mono.just(user);
                });
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
            System.out.println(user.toString());
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

    private Mono<UserRest> includeUserAlbums(UserRest user, String jwt) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .port(8084)
                        .path("/albums")
                        .build())
                .header("Authorization", jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return Mono.error(new RuntimeException("Albums not found for user."));
                })
                .onStatus(HttpStatusCode::is5xxServerError, reponse ->
                        Mono.error(new RuntimeException("server error while fetching albums")))
                .bodyToFlux(AlbumRest.class)
                .collectList()
                .map(albums -> {
                    user.setAlbums(albums);
                    return user;
                })
                .onErrorResume(throwable -> {
                    logger.error(throwable.getMessage(), throwable);
                    return Mono.just(user);
                });
    }
}
