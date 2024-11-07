package com.ticketingapp.auth.service;

import com.ticketingapp.auth.dto.RegisterRequest;
import com.ticketingapp.auth.model.*;
import com.ticketingapp.auth.repository.UserRepository;
import com.ticketingapp.config.JWTService;
import com.ticketingapp.shared.dto.SuccessDto;
import com.ticketingapp.shared.exeptions.ValueNotFoundForIdException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        boolean emailExists = userRepository.existsByEmail(registerRequest.getEmail());

        if (emailExists) {
            return AuthenticationResponse.builder().response("Email already exists!").build();
        }

        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.isAdmin() ? Role.ADMIN : Role.USER)
                .active(true)
                .build();


        userRepository.save(user);
        int tokenLifespan = 1000 * 60 * 60 * 5;
        var jwtToken = jwtService.generateToken(user, tokenLifespan);
        return AuthenticationResponse.builder().response(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(),
                authenticationRequest.getPassword()
        ));
        var user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow();
        int tokenLifespan = authenticationRequest.isRememberMe() ? (1000 * 60 * 60 * 5) : (1000 * 60 * 60 * 24 * 14);
        var jwtToken = jwtService.generateToken(user, tokenLifespan);
        return AuthenticationResponse.builder().response(jwtToken).build();
    }

    public User getUserWithToken(String authHeader) {
        String token = authHeader.substring(7);
        return userRepository.findByEmail(jwtService.extractUsername(token)).orElse(null);
    }

    public User updateUser(UUID userId, Map<String, Object> updateFields) {
        Optional<User> existingUserOptional = userRepository.findById(userId);

        if (existingUserOptional.isPresent()) {
            updateFields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(User.class, key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, existingUserOptional.get(), value);
            });
            return userRepository.save(existingUserOptional.get());
        } else {
            throw new IllegalArgumentException("User with id: " + userId + "can't be found");
        }

    }

    public List<User> getUserBySearchEmail(String email) {
        return userRepository.findUsersBySearchEmail(email);
    }


    public AuthenticationResponse generateForgotPasswordToken(String email) {
        var user = userRepository.findByEmail(email).orElseThrow();
        String ten_minutes_token = jwtService.generateToken(user, 1000 * 60 * 10);

//        MUST CHANGE
        String deploy_link = "https://quest.cleancode.ro/reset_CleanCode_password";
        String local_link = "http://localhost:5173/reset_CleanCode_password";
        String resetLink = deploy_link + "/" + ten_minutes_token + "/" + user.getId();

        String subject = "CleanCode Password Reset Request";
        String body = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
                + "You have requested to reset your password. Your one-time link is: \n\n" + resetLink + "\n\n"
                + "Please use this password to reset your password within the next 10 minutes.\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Sincerely,\n"
                + "CleanCode Team";

        // Send the email
        emailService.sendEmail(user.getEmail(), subject, body);

        return new AuthenticationResponse(resetLink);
    }

    public AuthenticationResponse resetUserPassword(String password, String authHeader) {
        String token = authHeader.substring(7);
        User user = userRepository.findByEmail(jwtService.extractUsername(token)).orElseThrow(() -> new ValueNotFoundForIdException("User token expired", null ));

        String new_password = passwordEncoder.encode(password);
        user.setPassword((new_password));
        userRepository.save(user);

        return new AuthenticationResponse("Password reseted successfully");
    }

    public SuccessDto delete(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ValueNotFoundForIdException("User", userId ));
        user.setActive(false);
        userRepository.save(user);

        return new SuccessDto(true);
    }
}
