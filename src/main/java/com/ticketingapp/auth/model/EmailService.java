package com.ticketingapp.auth.model;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;
import org.json.JSONObject;

@Component
public class EmailService {



    private String oauth2AccessToken;
    private long tokenExpiryTime;  // Store token expiry time in milliseconds

    public EmailService() {
        // Initialize token to null, will be requested later
        this.oauth2AccessToken = null;
        this.tokenExpiryTime = 0;
    }

    /**
     * Method to get OAuth2 token using Client Credentials flow
     */
    private void requestOAuth2Token() throws IOException {
        if (oauth2AccessToken == null || new Date().getTime() >= tokenExpiryTime) {
            System.out.println("Requesting new OAuth2 token...");

            // Prepare the request body
            String requestBody = "client_id=" + clientId
                    + "&client_secret=" + clientSecret
                    + "&grant_type=client_credentials"
                    + "&scope=" + scope;

            // Send a POST request to get the token
            URL url = new URL(tokenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

            // Read the response
            Scanner scanner = new Scanner(conn.getInputStream());
            String response = "";
            while (scanner.hasNext()) {
                response += scanner.nextLine();
            }
            scanner.close();

            System.out.println("response " + response);

            // Parse the JSON response to extract the access token and expiry time
            JSONObject jsonResponse = new JSONObject(response);
            oauth2AccessToken = jsonResponse.getString("access_token");
            int expiresIn = jsonResponse.getInt("expires_in");

            // Set the token expiry time in milliseconds
            tokenExpiryTime = new Date().getTime() + (expiresIn * 1000);

            System.out.println("OAuth2 token retrieved successfully.");
        } else {
            System.out.println("Using cached OAuth2 token.");
        }
    }

    /**
     * Method to fetch unread emails from the Outlook account
     */
    public void fetchUnreadEmails() {
        try {
            // Ensure we have a valid OAuth2 token before proceeding
            requestOAuth2Token();

            // Setup IMAP properties
            Properties props = new Properties();
            props.put("mail.store.protocol", "imap");
            props.put("mail.imap.host", "outlook.office365.com");
            props.put("mail.imap.port", "993");
            props.put("mail.imap.ssl.enable", "true");

            Session session = Session.getInstance(props);
            Store store = session.getStore("imap");

            // Connect to the IMAP store using the OAuth2 token instead of a password
            store.connect("outlook.office365.com", username, oauth2AccessToken);

            // Access the inbox folder and fetch unread emails
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Fetch unread messages
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            System.out.println("Number of unread emails: " + messages.length);

            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Sent Date: " + message.getSentDate());
            }

            // Close the folder and store
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch emails: " + e.getMessage());
        }
    }

    /**
     * Scheduled method to run every 5 minutes and fetch unread emails
     */
    @Scheduled(fixedRate = 300000)  // Runs every 5 minutes (300,000 ms)
    public void scheduledEmailFetch() {
        fetchUnreadEmails();
    }
}



