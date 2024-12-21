package com.ticketingapp.tickets.dto;

import com.ticketingapp.auth.dto.UserDto;
import com.ticketingapp.tickets.model.Status;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateTicketDto(
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
