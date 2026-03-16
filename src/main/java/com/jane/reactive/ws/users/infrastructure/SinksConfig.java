package com.jane.reactive.ws.users.infrastructure;

import com.jane.reactive.ws.users.presentation.model.UserRest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class SinksConfig {

    @Bean
    public Sinks.Many<UserRest> createUserSink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
