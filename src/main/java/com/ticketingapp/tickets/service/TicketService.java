package com.ticketingapp.tickets.service;


import com.ticketingapp.auth.model.User;
import com.ticketingapp.auth.repository.UserRepository;
import com.ticketingapp.auth.service.UserService;
import com.ticketingapp.tickets.dto.CreteTicketDto;
import com.ticketingapp.tickets.model.Status;
import com.ticketingapp.tickets.model.Ticket;
import com.ticketingapp.tickets.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    public Ticket createTicket(CreteTicketDto dto) {

        Ticket ticket = Ticket.builder()
                .title(dto.title())
                .status(Status.UNRESOLVED)
                .trackingNumber(dto.trackingNumber())
                .phoneNumber(dto.phoneNumber())
                .mailBody(dto.mailBody())
                .createdBy(dto.createdBy())
                .build();

        return ticketRepository.save(ticket);
    }
}
