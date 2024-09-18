package com.ticketingapp.tickets.service;


import com.ticketingapp.auth.model.User;
import com.ticketingapp.auth.repository.UserRepository;
import com.ticketingapp.auth.service.UserService;
import com.ticketingapp.shared.exeptions.ValueNotFoundForIdException;
import com.ticketingapp.tickets.dto.CreteTicketDto;
import com.ticketingapp.tickets.dto.TicketDto;
import com.ticketingapp.tickets.model.Status;
import com.ticketingapp.tickets.model.Ticket;
import com.ticketingapp.tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static com.ticketingapp.shared.mappers.TicketMapper.ticketToDtoMapper;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public TicketDto createTicket(CreteTicketDto dto) {
        User responsibleUser = userRepository.findById(dto.createdBy().id()).orElseThrow(() -> new ValueNotFoundForIdException("User", dto.createdBy().id()));

        Ticket ticket = Ticket.builder()
                .title(dto.title())
                .status(Status.UNRESOLVED)
                .trackingNumber(dto.trackingNumber())
                .phoneNumber(dto.phoneNumber())
                .mailBody(dto.mailBody())
                .createdBy(responsibleUser)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        return ticketToDtoMapper(ticketRepository.save(ticket));
    }
}
