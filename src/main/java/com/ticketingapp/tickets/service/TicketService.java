package com.ticketingapp.tickets.service;


import com.ticketingapp.auth.model.User;
import com.ticketingapp.auth.repository.UserRepository;
import com.ticketingapp.shared.dto.PageRequestDto;
import com.ticketingapp.shared.dto.PageResponseDto;
import com.ticketingapp.shared.exeptions.ValueNotFoundForIdException;
import com.ticketingapp.tickets.dto.CreteTicketDto;
import com.ticketingapp.tickets.dto.TicketDto;
import com.ticketingapp.tickets.dto.UpdateTicketDto;
import com.ticketingapp.tickets.model.Status;
import com.ticketingapp.tickets.model.Ticket;
import com.ticketingapp.tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.ticketingapp.shared.mappers.PageableMappers.pageRequestToDtoMapper;
import static com.ticketingapp.shared.mappers.TicketMapper.ticketToDtoMapper;
import static com.ticketingapp.shared.mappers.TicketMapper.ticketsToDtoMapper;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public PageResponseDto<List<TicketDto>> getTickets(PageRequestDto pageRequestDto) {
        Pageable page = pageRequestToDtoMapper(pageRequestDto);
        Page<Ticket> tickets = ticketRepository.findAll(page);

        return new PageResponseDto<>(
                ticketsToDtoMapper(tickets.stream().toList()),
                tickets.getTotalElements(),
                tickets.getNumber(),
                tickets.getTotalPages()
                );
    }

    public TicketDto createTicket(CreteTicketDto dto) {
        User user = userRepository.findById(dto.createdBy().id()).orElseThrow(() -> new ValueNotFoundForIdException("User", dto.createdBy().id()));

        Ticket ticket = Ticket.builder()
                .title(dto.title())
                .status(Status.UNRESOLVED)
                .trackingNumber(dto.trackingNumber())
                .phoneNumber(dto.phoneNumber())
                .mailBody(dto.mailBody())
                .createdBy(user)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        return ticketToDtoMapper(ticketRepository.save(ticket));
    }

    public TicketDto updateTicket(UpdateTicketDto dto) {
        User userOwner = userRepository.findById(dto.createdBy().id()).orElseThrow(() -> new ValueNotFoundForIdException("User", dto.createdBy().id()));
        User userAssigned = userRepository.findById(dto.assignedTo().id()).orElse(null);

        Ticket ticket = Ticket.builder()
                .id(dto.id())
                .title(dto.title())
                .status(Status.UNRESOLVED)
                .trackingNumber(dto.trackingNumber())
                .workOrderNumber(dto.workOrderNumber())
                .phoneNumber(dto.phoneNumber())
                .address(dto.address())
                .mailBody(dto.mailBody())
                .createdBy(userOwner)
                .assignedTo(userAssigned)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        return ticketToDtoMapper(ticketRepository.save(ticket));
    }
}
