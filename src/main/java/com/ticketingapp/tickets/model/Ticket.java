package com.ticketingapp.tickets.model;

import com.ticketingapp.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Status status;

    private long trackingNumber;

    private long workOrderNumber;

    private short phoneNumber;

    private String address;

    @Column(length = 1000)
    private String mailBody;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDate createdAt;

    private LocalDate updatedAt;
}
