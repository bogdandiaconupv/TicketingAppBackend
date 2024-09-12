package com.ticketingapp.tickets.model;

public enum Status {
    UNRESOLVED("UNRESOLVED"),
    PENDING("PENDING"),
    NO_RESPONSE("NO_RESPONSE"),
    RESOLVED("RESOLVED");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

