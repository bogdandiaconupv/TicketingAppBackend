package com.ticketingapp.auth.model;

public class Email {
    private String subject;
    private String from;
    private String to;
    private String receivedDate;
    private int size;
    private String flags;
    private String contentType;
    private String body;

    // Getters and setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getReceivedDate() { return receivedDate; }
    public void setReceivedDate(String receivedDate) { this.receivedDate = receivedDate; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getFlags() { return flags; }
    public void setFlags(String flags) { this.flags = flags; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
