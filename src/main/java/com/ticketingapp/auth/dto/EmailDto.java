package com.ticketingapp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
 @AllArgsConstructor
@NoArgsConstructor
public class EmailDto {
    private String subject;
    private String from;
    private String to;
    private String receivedDate;
    private int size;
    private String flags;
    private String contentType;
    private String body;
}
