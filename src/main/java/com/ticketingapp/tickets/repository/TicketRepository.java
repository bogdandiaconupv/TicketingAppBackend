package com.ticketingapp.tickets.repository;

import com.ticketingapp.tickets.model.Status;
import com.ticketingapp.tickets.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("SELECT t FROM Ticket t " +
            "WHERE (:status IS NULL OR t.status = :status) " +
            "AND (:createdAt IS NULL OR t.createdAt = :createdAt) " +
            "AND (:updatedAt IS NULL OR t.updatedAt = :updatedAt) " +
            "AND (:createdBy IS NULL OR t.createdBy.id = :createdBy) " +
            "AND (:assignedTo IS NULL OR t.assignedTo.id = :assignedTo) " +
            "AND (:trackingNumber IS NULL OR t.trackingNumber = :trackingNumber) " +
            "AND (:workOrderNumber IS NULL OR t.workOrderNumber = :workOrderNumber) "
    )
    Page<Ticket> findByFilters(@Param("status") Status status,
                               @Param("createdAt") LocalDate created_at,
                               @Param("updatedAt") LocalDate updatedAt,
                               @Param("createdBy") UUID createdBy,
                               @Param("assignedTo") UUID assignedTo,
                               @Param("trackingNumber") Long trackingNumber,
                               @Param("workOrderNumber") Long workOrderNumber,
                               Pageable pageable);


}
