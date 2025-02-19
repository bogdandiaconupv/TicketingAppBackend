package com.ticketingapp.auth.controller;

import com.ticketingapp.auth.dto.EmailDto;
import com.ticketingapp.auth.dto.RegisterRequest;
import com.ticketingapp.auth.model.*;
import com.ticketingapp.auth.service.EmailService;
import com.ticketingapp.auth.service.UserService;
import com.ticketingapp.shared.dto.SuccessDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/auth/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(userService.authenticate(authenticationRequest));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/auth/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid RegisterRequest registerRequest){
        return ResponseEntity.ok(userService.register(registerRequest));
    }

    @GetMapping("/getUserWithToken")
    public ResponseEntity<User> getUserWithToken(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok(userService.getUserWithToken(authHeader));
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<User>> getUserBySearchEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.getUserBySearchEmail(email));
    }

    @GetMapping("/auth/forgotPassword/{email}")
    public ResponseEntity<AuthenticationResponse> forgotPasswordRequest(@PathVariable String email, HttpServletRequest httpServletRequest ){
        return ResponseEntity.ok(userService.generateForgotPasswordToken(email, httpServletRequest));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable UUID userId, @RequestBody Map<String, Object> updateFields){
        return ResponseEntity.ok( userService.updateUser(userId, updateFields));
    }

    @PatchMapping("/reset_CleanCode_password")
    public ResponseEntity<AuthenticationResponse> resetUserPassword(@RequestBody Map<String, String> requestBody, @RequestHeader("Authorization") String authHeader ){
        return ResponseEntity.ok(userService.resetUserPassword( requestBody.get("password"), authHeader));
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessDto> deleteUser(@PathVariable("userId") UUID userId){
        return ResponseEntity.ok(userService.delete(userId));
    }
    @GetMapping("/fetchTickets")
    public ResponseEntity<List<EmailDto>> getEmails() {
        List<EmailDto> unreadEmailDtos = emailService.fetchUnreadEmails();
        return ResponseEntity.ok(unreadEmailDtos);
    }

}

