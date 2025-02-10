package com.ticketingapp.auth.service;

import com.ticketingapp.auth.model.Email;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmailService {
    private final String username = "projectcleancode@gmail.com";
    private final String password = "vfdb xyof uxyc pkiy";
    private final Properties props;

    public EmailService() {
        // Setup properties for the SMTP server (Gmail)
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587"); // Use port 587 for TLS
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
    }

    public void sendEmail(String to, String subject, String body) {
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully.");

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    public List<Email> fetchUnreadEmails() {
        List<Email> emailList = new ArrayList<>();
        try {
            // Use IMAP instead of POP3
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imaps");
            properties.put("mail.imap.host", "imap.gmail.com");
            properties.put("mail.imap.port", "993");

            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect("imap.gmail.com", username, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            // Fetch SEEN messages using IMAP
            Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flag.SEEN), false));
            System.out.println("Total seen emails: " + messages.length);

            for (Message message : messages) {
                Email email = new Email();
                email.setFrom(InternetAddress.toString(message.getFrom()));
                email.setSubject(message.getSubject());
                email.setReceivedDate(message.getSentDate().toString());
                email.setSize(message.getSize());
                email.setFlags(message.getFlags().toString());
                email.setContentType(message.getContentType());

                // Get email content
                Object content = message.getContent();
                if (content instanceof String) {
                    email.setBody(content.toString());
                } else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    StringBuilder bodyContent = new StringBuilder();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        Part part = multipart.getBodyPart(i);
                        if (part.isMimeType("text/plain")) {
                            bodyContent.append(part.getContent().toString());
                        }
                    }
                    email.setBody(bodyContent.toString());
                }

                emailList.add(email);
            }

            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailList;
    }

    /**
     * Processes unread emails and extracts ticket-related information.
     * Returns a list of Object arrays, where each array contains:
     * [title, phoneNumber, address, trackingNumber, mailBody].
     */
    public List<Object[]> processUnreadEmailsForTickets() {
        List<Object[]> ticketDataList = new ArrayList<>();
        List<Email> unreadEmails = fetchUnreadEmails();

        for (Email email : unreadEmails) {
            String emailBody = email.getBody();

            // Extract ticket details from the email body
            String phoneNumber = extractValue(emailBody, "Phone Number:");
            String address = extractValue(emailBody, "Address:");
            String trackingNumber = extractValue(emailBody, "Tracking Number:");
            String problem = extractValue(emailBody, "Problem:");

            // Prepare the data as an Object array
            Object[] ticketData = new Object[]{
                    email.getSubject(), // title
                    phoneNumber,       // phoneNumber
                    address,           // address
                    trackingNumber,    // trackingNumber
                    problem            // mailBody
            };

            // Add the data to the list
            ticketDataList.add(ticketData);
        }

        return ticketDataList;
    }

    /**
     * Helper method to extract a value from the email body based on a key.
     */
    private String extractValue(String emailBody, String key) {
        int startIndex = emailBody.indexOf(key);
        if (startIndex == -1) {
            return null; // Key not found
        }
        startIndex += key.length();
        int endIndex = emailBody.indexOf("\n", startIndex);
        if (endIndex == -1) {
            endIndex = emailBody.length();
        }
        return emailBody.substring(startIndex, endIndex).trim();
    }
}