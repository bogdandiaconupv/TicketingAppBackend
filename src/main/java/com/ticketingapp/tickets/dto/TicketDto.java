package com.ticketingapp.tickets.dto;

import com.ticketingapp.auth.dto.UserDto;
import com.ticketingapp.tickets.model.Status;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TicketDto (
        UUID id,
        String title,
        Status status,
        String trackingNumber,
        String workOrderNumber,
        String phoneNumber,
        String address,
        String mailBody,
        UserDto assignedTo,
        UserDto createdBy,
        LocalDate createdAt,
        LocalDate updatedAt,
        boolean active
) {}
