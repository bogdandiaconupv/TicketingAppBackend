package com.ticketingapp.tickets.repository;

import com.ticketingapp.tickets.model.Status;
import com.ticketingapp.tickets.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("SELECT t FROM Ticket t " +
            "WHERE (:status IS NULL OR :status='undefined' OR t.status = :status) " +
//            "AND (:createdAtStart IS NULL OR :createdAtEnd IS NULL OR t.createdAt BETWEEN :createdAtStart AND :createdAtEnd) " +
            //            "AND (:updatedAtStart IS NULL OR :updatedAtEnd IS NULL OR :updatedAtStart= '' OR :updatedAtEnd ='' OR t.updatedAt BETWEEN :updatedAtStart AND :updatedAtEnd)"+
            "AND (t.createdAt >= COALESCE(:createdAtStart, t.createdAt) AND t.createdAt <= COALESCE(:createdAtEnd, t.createdAt)) " +
            //            "AND (t.createdAt BETWEEN :createdAtStart AND :createdAtEnd)" +
//            "AND (t.updatedAt BETWEEN :updatedAtStart AND :updatedAtEnd)" +
            "AND (:createdBy IS NULL OR :createdBy ='' OR t.createdBy.id IN :createdBy) " +
            "AND (:assignedTo IS NULL OR :assignedTo ='' OR t.assignedTo.id IN :assignedTo) " +
            "AND (:trackingNumber IS NULL OR :trackingNumber = '' OR LOWER(t.trackingNumber) LIKE LOWER(CONCAT('%', :trackingNumber, '%'))) " +
            "AND (:workOrderNumber IS NULL OR :workOrderNumber = '' OR LOWER(t.workOrderNumber) LIKE LOWER(CONCAT('%', :workOrderNumber, '%'))) "
    )
    Page<Ticket> findByFilters(@Param("status") Status status,
                               @Param("createdAtStart") LocalDate createdAtStart,
                               @Param("createdAtEnd") LocalDate createdAtEnd,
//                               @Param("updatedAtStart") LocalDate updatedAtStart,
//                               @Param("updatedAtEnd") LocalDate updatedAtEnd,
                               @Param("createdBy") List<UUID> createdBy,
                               @Param("assignedTo") List<UUID> assignedTo,
                               @Param("trackingNumber") String trackingNumber,
                               @Param("workOrderNumber") String workOrderNumber,
                               Pageable pageable);


}
