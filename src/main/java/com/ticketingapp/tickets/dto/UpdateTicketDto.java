package com.ticketingapp.tickets.dto;

import com.ticketingapp.auth.dto.UserDto;
import com.ticketingapp.tickets.model.Status;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record UpdateTicketDto(
        UUID id,
        String title,
        Status status,
        long trackingNumber,
        long workOrderNumber,
        short phoneNumber,
        String address,
        String mailBody,
        UserDto assignedTo,
        UserDto createdBy,
        LocalDate createdAt,
        LocalDate updatedAt,
        boolean active
) {}
