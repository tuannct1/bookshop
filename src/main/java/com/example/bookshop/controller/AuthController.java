package com.example.bookshop.controller;

import com.example.bookshop.dto.request.LoginRequest;
import com.example.bookshop.dto.request.RefreshTokenRequest;
import com.example.bookshop.dto.request.RegisterRequest;
import com.example.bookshop.dto.response.LoginResponse;
import com.example.bookshop.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.text.ParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Gọi AuthService để xử lý logic lưu vào Database
            String responseMessage = authService.register(registerRequest);
            
            // Trả về mã 201 (CREATED) kèm câu thông báo thành công
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
            
        } catch (IllegalArgumentException e) {
            // Bắt lỗi logic nghiệp vụ từ AuthService (Ví dụ: Trùng username)
            // Trả về mã 400 (BAD REQUEST) kèm câu báo lỗi
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (Exception e) {
            // Bắt các lỗi hệ thống (như lỗi database, không tìm thấy Role...)
            // Trả về mã 500 (INTERNAL SERVER ERROR)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }
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
        // Gọi thẳng vào hàm refreshToken trong AuthService mà bạn vừa viết
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    } 
}