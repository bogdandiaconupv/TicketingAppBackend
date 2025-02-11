package com.ticketingapp.auth.service;

import com.ticketingapp.auth.dto.EmailDto;
import org.springframework.beans.factory.annotation.Value;
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
    private final String username;
    private final String password;
    private final Properties props;

    private final String mailStoreProtocol;
    private final String mailImapHost;
    private final String mailImapPort;

    public EmailService(
            @Value("${MAIL_SMTP_AUTH}") String smtpAuth,
            @Value("${MAIL_SMTP_STARTTLS_ENABLE}") String starttlsEnable,
            @Value("${MAIL_SMTP_HOST}") String smtpHost,
            @Value("${MAIL_SMTP_PORT}") String smtpPort,
            @Value("${MAIL_SMTP_SSL_TRUST}") String sslTrust,
            @Value("${EMAIL_SENDER_ADDRESS}") String emailSenderAddress,
            @Value("${EMAIL_PASSWORD}") String emailPassword,
            @Value("${MAIL_STORE_PROTOCOL}") String mailStoreProtocol,
            @Value("${MAIL_IMAP_HOST}") String mailImapHost,
            @Value("${MAIL_IMAP_PORT}") String mailImapPort
    ) {
        this.username = emailSenderAddress;
        this.password = emailPassword;

        this.mailStoreProtocol = mailStoreProtocol;
        this.mailImapHost = mailImapHost;
        this.mailImapPort = mailImapPort;

        props = new Properties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.trust", sslTrust);
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

    public List<EmailDto> fetchUnreadEmails() {
        List<EmailDto> emailDtoList = new ArrayList<>();
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", mailStoreProtocol);
            properties.put("mail.imap.host", mailImapHost);
            properties.put("mail.imap.port", mailImapPort);

            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("imaps");
            store.connect("imap.gmail.com", username, password);

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            // Fetch SEEN messages using IMAP
            Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flag.SEEN), false));
            System.out.println("Total seen emails: " + messages.length);

            for (Message message : messages) {
                EmailDto emailDto = new EmailDto();
                emailDto.setFrom(InternetAddress.toString(message.getFrom()));
                emailDto.setSubject(message.getSubject());
                emailDto.setReceivedDate(message.getSentDate().toString());
                emailDto.setSize(message.getSize());
                emailDto.setFlags(message.getFlags().toString());
                emailDto.setContentType(message.getContentType());

                // Get email content
                Object content = message.getContent();
                if (content instanceof String) {
                    emailDto.setBody(content.toString());
                } else if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    StringBuilder bodyContent = new StringBuilder();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        Part part = multipart.getBodyPart(i);
                        if (part.isMimeType("text/plain")) {
                            bodyContent.append(part.getContent().toString());
                        }
                    }
                    emailDto.setBody(bodyContent.toString());
                }

                emailDtoList.add(emailDto);
            }

            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailDtoList;
    }
}