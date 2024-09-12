package com.ticketingapp.tickets.dto;

import com.ticketingapp.auth.model.User;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

@Validated
public record CreteTicketDto(
        @NotBlank(message = "Title cannot be blank")
        String title,

        long trackingNumber,

        short phoneNumber,

        @NotBlank(message = "Mail body cannot be blank")
        @Size(max = 1000, message = "Mail body must be less than 1000 characters")
        String mailBody,

        @NotNull(message = "CreatedBy cannot be null")
        User createdBy
) {}
