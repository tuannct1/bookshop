package com.example.bookshop.controller;

import com.example.bookshop.dto.request.LoginRequest;
import com.example.bookshop.dto.request.RefreshTokenRequest;
import com.example.bookshop.dto.request.RegisterRequest;
import com.example.bookshop.dto.request.VerifyOtpRequest;
import com.example.bookshop.dto.response.LoginResponse;
import com.example.bookshop.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String responseMessage = authService.register(registerRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }
    }
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyOtpRequest request) {
        String response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {  
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String authHeader) throws ParseException {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
    }   
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) throws Exception {
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    } 
}