package com.ticketingapp.tickets.controller;


import com.ticketingapp.shared.dto.PageRequestDto;
import com.ticketingapp.shared.dto.PageResponseDto;
import com.ticketingapp.shared.dto.SuccessDto;
import com.ticketingapp.tickets.dto.CreateTicketDto;
import com.ticketingapp.tickets.dto.TicketDto;
import com.ticketingapp.tickets.dto.UpdateTicketDto;
import com.ticketingapp.tickets.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/tickets")
@RestController
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PageResponseDto<List<TicketDto>>> getTickets(PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(ticketService.getTickets(pageRequestDto));
    }


    @GetMapping("/{ticketId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TicketDto> getTicket(@PathVariable("ticketId") UUID ticketId) {
        return ResponseEntity.ok(ticketService.getTicketById(ticketId));
    }


    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDto> createTicket(@RequestBody @Validated CreateTicketDto dto) {
        return ResponseEntity.ok(ticketService.createTicket(dto));
    }

    @PutMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDto> updateTicket(@PathVariable("ticketId") UUID ticketId, @RequestBody @Validated UpdateTicketDto dto) {
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, dto));
    }

    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessDto> deleteTicket(@PathVariable("ticketId") UUID ticketId) {
        return ResponseEntity.ok(ticketService.deleteTicket(ticketId));
    }


}
