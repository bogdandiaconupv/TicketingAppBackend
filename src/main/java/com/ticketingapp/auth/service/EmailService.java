package com.ticketingapp.auth.service;

import com.ticketingapp.auth.model.Email;
import org.springframework.stereotype.Component;


import javax.mail.*;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", "pop.gmail.com");
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");

            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("pop3s");
            store.connect("pop.gmail.com", username, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // Search for unseen messages
            Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flag.SEEN), false));
            for (Message message : messages) {
                Email email = new Email();

                email.setFrom(InternetAddress.toString(message.getFrom()));
                email.setSubject(message.getSubject());
                email.setReceivedDate(message.getSentDate().toString());
                email.setSize(message.getSize());
                email.setFlags(message.getFlags().toString());
                email.setContentType(message.getContentType());

                // Get the body content of the email (handling plain text and HTML)
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

                emailList.add(email); // Add populated Email object to list
            }

            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailList;
    }
}
