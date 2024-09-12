package com.ticketingapp.tickets.controller;


import com.ticketingapp.tickets.dto.CreteTicketDto;
import com.ticketingapp.tickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/tickets")
@RestController
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody @Validated CreteTicketDto dto){
        return ResponseEntity.ok(ticketService.createTicket(dto));
    }
}
