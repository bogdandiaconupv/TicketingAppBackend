package com.ticketingapp.auth.dto;

import com.ticketingapp.auth.model.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        String profileImageUrl
) {}
