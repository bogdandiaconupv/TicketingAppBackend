package com.ticketingapp.tickets.service;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.ticketingapp.auth.model.Email;
import com.ticketingapp.auth.service.EmailService;
import com.ticketingapp.tickets.dto.CreteTicketDto;
import com.ticketingapp.tickets.service.TicketService;
import com.ticketingapp.auth.dto.UserDto;
import com.ticketingapp.auth.model.User;
import com.ticketingapp.auth.repository.UserRepository;
import com.ticketingapp.tickets.model.Status;
import com.ticketingapp.auth.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailToTicketScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;
//    @PostConstruct
//    public void onApplicationStart() {
//        processUnreadEmailsAndCreateTickets(); // Fetch emails immediately on startup
//    }
    @Scheduled(fixedRate = 60000) // 600000 milliseconds = 1 minutes
    public void processUnreadEmailsAndCreateTickets() {
        List<Email> unreadEmails = emailService.fetchUnreadEmails();

        if (unreadEmails.isEmpty()) {
            System.out.println("No new unread emails found.");
            return;
        }

        for (Email email : unreadEmails) {
            System.out.println("\nProcessing email: " + email.getSubject());
            System.out.println("From: " + email.getFrom());
            System.out.println("Raw Email Body:\n" + email.getBody());

            // Extract values
            String emailBody = email.getBody();
            String phoneNumber = extractValue(emailBody, "Phone number:");
            String address = extractValue(emailBody, "Address:");
            String trackingNumber = extractValue(emailBody, "Tracking Number:");
            String problem = extractValue(emailBody, "Problem:");

            // Log extracted values
            System.out.println("Extracted Phone Number: " + phoneNumber);
            System.out.println("Extracted Address: " + address);
            System.out.println("Extracted Tracking Number: " + trackingNumber);
            System.out.println("Extracted Problem: " + problem);

            // Validate extracted values
            if (phoneNumber == null || address == null || trackingNumber == null || problem == null) {
                System.out.println("❌ Missing required fields, skipping email.");
                continue;
            }

            // Extract the email from the "From" field
            String senderEmail = extractEmail(email.getFrom());
            System.out.println("Extracted Sender Email: " + senderEmail);

            Optional<User> senderOptional = userRepository.findByEmail(senderEmail);
            if (senderOptional.isEmpty()) {
                System.out.println("❌ User not found for email: " + senderEmail);
                continue;
            }

            User sender = senderOptional.get();
            UserDto senderDto = new UserDto(
                    sender.getId(),
                    sender.getFirstName(),
                    sender.getLastName(),
                    sender.getEmail(),
                    sender.getRole(),
                    sender.getProfileImageUrl(),
                    sender.isActive()
            );

            // Create the ticket DTO
            CreteTicketDto createTicketDto = new CreteTicketDto(
                    email.getSubject(),
                    Status.UNRESOLVED,
                    address,
                    trackingNumber,
                    phoneNumber,
                    problem,
                    senderDto
            );

            // Log ticket creation attempt
            System.out.println("✅ Creating ticket: " + createTicketDto.title());

            // Create the ticket
            ticketService.createTicket(createTicketDto);

            System.out.println("✅ Ticket successfully created for: " + email.getSubject());
        }

        System.out.println("Finished processing unread emails.");
    }


    /**
     * Helper method to extract a value from the email body based on a key.
     */
    private String extractValue(String emailBody, String key) {
        int startIndex = emailBody.indexOf(key);
        if (startIndex == -1) return null; // Key not found

        startIndex += key.length();

        // Ensure we find the next newline, handling both "\n" and "\r\n"
        int endIndex = emailBody.indexOf("\n", startIndex);
        if (endIndex == -1) endIndex = emailBody.indexOf("\r", startIndex);
        if (endIndex == -1) endIndex = emailBody.length();

        return emailBody.substring(startIndex, endIndex).trim();
    }
    private String extractEmail(String fromField) {
        if (fromField.contains("<") && fromField.contains(">")) {
            return fromField.substring(fromField.indexOf("<") + 1, fromField.indexOf(">")).trim();
        }
        return fromField.trim();
    }
}