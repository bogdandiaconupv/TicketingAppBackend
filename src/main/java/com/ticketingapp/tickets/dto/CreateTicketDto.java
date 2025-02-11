package com.ticketingapp.tickets.dto;

import com.ticketingapp.auth.dto.UserDto;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;
import com.ticketingapp.tickets.model.Status;


@Validated
public record CreateTicketDto(
        @NotBlank(message = "Title cannot be blank")
        String title,

        Status status,

        String address,

        String trackingNumber,

        String phoneNumber,

        @NotBlank(message = "Mail body cannot be blank")
        @Size(max = 1000, message = "Mail body must be less than 1000 characters")
        String mailBody,

        @NotNull(message = "CreatedBy cannot be null")
        UserDto createdBy
) {}
