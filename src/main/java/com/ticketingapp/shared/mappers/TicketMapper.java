package com.ticketingapp.shared.mappers;

import com.ticketingapp.tickets.dto.TicketDto;
import com.ticketingapp.tickets.model.Ticket;

import java.util.List;
import java.util.stream.Collectors;

import static com.ticketingapp.shared.mappers.UserMapper.userToDtoMapper;

public class TicketMapper {
    public static TicketDto ticketToDtoMapper(Ticket ticket){
        return TicketDto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .status(ticket.getStatus())
                .trackingNumber(ticket.getTrackingNumber())
                .workOrderNumber(ticket.getWorkOrderNumber())
                .phoneNumber(ticket.getPhoneNumber())
                .address(ticket.getAddress())
                .mailBody(ticket.getMailBody())
                .assignedTo(ticket.getAssignedTo() != null ? userToDtoMapper(ticket.getAssignedTo()) : null)
                .createdBy(userToDtoMapper(ticket.getCreatedBy()))
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    public static List<TicketDto> ticketsToDtoMapper(List<Ticket> tickets){
        return tickets.stream().map(TicketMapper::ticketToDtoMapper).collect(Collectors.toList());
    }
}
