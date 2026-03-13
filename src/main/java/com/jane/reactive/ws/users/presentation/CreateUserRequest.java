package com.jane.reactive.ws.users.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class CreateUserRequest {

    @NotBlank(message="First name cannot be empty")
    @Size(min = 2, max = 50, message="First name cannot be shorter than 2 and longer than 50 characters")
    private String firstName;

    @NotBlank(message="Last name cannot be empty")
    @Size(min = 2, max = 50, message="Last name cannot be shorter than 2 and longer than 50 characters")
    private String lastName;

    @NotBlank(message="Email cannot be empty")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message="Password cannot be empty")
    @Size(min = 8, max = 16, message="Password cannot be shorter than 8 and longer than 16 characters")
    private String password;;
}
