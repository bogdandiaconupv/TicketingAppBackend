package com.ticketingapp.tickets.controller;


import com.ticketingapp.shared.dto.PageRequestDto;
import com.ticketingapp.shared.dto.PageResponseDto;
import com.ticketingapp.shared.dto.SuccessDto;
import com.ticketingapp.tickets.dto.CreteTicketDto;
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
    public ResponseEntity<PageResponseDto<List<TicketDto>>> getTickets(PageRequestDto pageRequestDto){
        return ResponseEntity.ok(ticketService.getTickets(pageRequestDto));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDto> createTicket(@RequestBody @Validated CreteTicketDto dto){
        return ResponseEntity.ok(ticketService.createTicket(dto));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TicketDto> updateTicket(@RequestBody @Validated UpdateTicketDto dto){
        return ResponseEntity.ok(ticketService.updateTicket(dto));
    }

    @DeleteMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessDto> deleteTicket(@PathVariable("ticketId") UUID ticketId){
        return ResponseEntity.ok(ticketService.deleteTicket(ticketId));
    }


}
