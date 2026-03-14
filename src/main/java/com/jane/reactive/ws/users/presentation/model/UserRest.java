package com.jane.reactive.ws.users.presentation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
public class UserRest {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}
