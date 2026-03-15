package com.jane.reactive.ws.users.service;

import com.jane.reactive.ws.users.data.UserEntity;
import com.jane.reactive.ws.users.data.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @Override
    public Mono<Map<String, String>> authenticate(String username, String password) {
        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .then(getUserDetails(username))
                .map(this::createResponse);
    }

    private Map<String, String> createResponse(UserEntity userEntity) {
        Map<String, String> response = new HashMap<>();
        response.put("userId", userEntity.getId().toString());
        response.put("token", jwtService.generateJwt(userEntity.getId().toString()));
        return response;
    }

    private Mono<UserEntity> getUserDetails(String username) {
        return userRepository.findByEmail(username);
    }
}
