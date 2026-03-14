package com.jane.reactive.ws.users.presentation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class AuthenticationRequest {
    private String email;
    private String password;
}
