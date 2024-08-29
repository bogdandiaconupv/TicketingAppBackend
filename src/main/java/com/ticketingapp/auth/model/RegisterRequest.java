package com.ticketingapp.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class RegisterRequest {
    @Size(min = 2, message = "First name must be at least 2 characters long")
    @NotBlank(message = "First name is required and cannot be blank")
    private String firstName;

    @Size(min = 2, message = "First name must be at least 2 characters long")
    @NotBlank(message = "Last name is required and cannot be blank")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 3, message = "Password must be at least 3 characters long")
    @NotBlank(message = "Password is required and cannot be blank")
    private String password;

    @NotNull(message = "Role is required")
    private boolean admin;
}
