package com.ticketingapp.auth.service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class OAuth2Authenticator extends Authenticator {

    private final String username;
    private final String oauth2AccessToken;

    public OAuth2Authenticator(String username, String oauth2AccessToken) {
        this.username = username;
        this.oauth2AccessToken = oauth2AccessToken;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        // Return null because we will use OAuth2 token as password.
        return new PasswordAuthentication(username, oauth2AccessToken);
    }
}
