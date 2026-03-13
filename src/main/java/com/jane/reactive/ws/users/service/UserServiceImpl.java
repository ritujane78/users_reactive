package com.jane.reactive.ws.users.service;

import com.jane.reactive.ws.users.data.UserEntity;
import com.jane.reactive.ws.users.data.UserRepository;
import com.jane.reactive.ws.users.presentation.CreateUserRequest;
import com.jane.reactive.ws.users.presentation.UserRest;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserRest> createUser(Mono<CreateUserRequest> createUserRequest) {
        return createUserRequest
                .mapNotNull(this::convertToUserEntity)
                .flatMap(userRepository::save)
                .map(this::convertToUserRest);
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

    private UserEntity convertToUserEntity(CreateUserRequest createUserRequest) {
        UserEntity user = new UserEntity();
        BeanUtils.copyProperties(createUserRequest, user);

        return user;
    }

    private UserRest convertToUserRest(UserEntity userEntity) {
        UserRest userRest = new UserRest();
        BeanUtils.copyProperties(userEntity, userRest);
        return userRest;
    }
}
