package com.ticketingapp.auth.model;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

@Component
public class EmailService {

    private final String clientId = "876b7d12-3174-4e7b-9166-48660f554e1b";
    private final String clientSecret = "FYH8Q~dC8VgLFIj0Xi4-St.E5wmL_RIZ-9MERbQe";
    private final String tenantId = "9c60037f-7a21-4c4e-99fe-3bddbb247521";  // Your tenant ID
    private final String scope = "https://graph.microsoft.com/.default";
    private final String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";
    private final String graphApiUrl = "https://graph.microsoft.com/v1.0/me/mailFolders/inbox/messages?$filter=isRead eq false";
    private final String username = "bogdanCleanCode@outlook.com";  // Graph uses `me` so this is optional

    private String oauth2AccessToken;
    private long tokenExpiryTime;  // Store token expiry time in milliseconds

    public EmailService() {
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

            // Write request body
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read the response
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }

                // Parse the JSON response to extract the access token and expiry time
                JSONObject jsonResponse = new JSONObject(response.toString());
                oauth2AccessToken = jsonResponse.getString("access_token");
                int expiresIn = jsonResponse.getInt("expires_in");

                // Set the token expiry time in milliseconds
                tokenExpiryTime = new Date().getTime() + (expiresIn * 1000L);
                System.out.println("OAuth2 token retrieved successfully.");
            }
        } else {
            System.out.println("Using cached OAuth2 token.");
        }
    }

    /**
     * Fetch unread emails using Microsoft Graph API
     */
    public void fetchUnreadEmails() {
        try {
            // Ensure we have a valid OAuth2 token
            requestOAuth2Token();

            // Setup the connection to Microsoft Graph
            URL url = new URL(graphApiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + oauth2AccessToken);
            conn.setRequestProperty("Accept", "application/json");

            // Get the response
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }

                System.out.println("Response from Microsoft Graph: " + response.toString());

                // Parse the JSON response to extract email details
                JSONObject jsonResponse = new JSONObject(response.toString());
                jsonResponse.getJSONArray("value").forEach(item -> {
                    JSONObject message = (JSONObject) item;
                    System.out.println("Subject: " + message.getString("subject"));
                    System.out.println("From: " + message.getJSONObject("from").getJSONObject("emailAddress").getString("address"));
                    System.out.println("Received Date: " + message.getString("receivedDateTime"));
                });
            }
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
