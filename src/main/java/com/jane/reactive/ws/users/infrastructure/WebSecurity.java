package com.jane.reactive.ws.users.infrastructure;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurity {


    @Bean
    public SecurityWebFilterChain httpSecurityFilterChain(ServerHttpSecurity http,
                                                          ReactiveAuthenticationManager authenticationManager){
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, "/users").permitAll()
                        .pathMatchers(HttpMethod.POST, "/login").permitAll()
                        .anyExchange().authenticated())
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasicAuth -> httpBasicAuth.disable())
                .authenticationManager(authenticationManager)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
