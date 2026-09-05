package com.example.bookshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    
    @Async("mailTaskExecutor")
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom("congtuan12052005@gmail.com"); 
        message.setTo(toEmail);
        message.setSubject("Mã xác nhận đăng ký tài khoản BookShop");
        message.setText("Chào bạn,\n\n"
                + "Mã xác nhận (OTP) của bạn là: " + otpCode + "\n\n"
                + "Mã này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ BookShop.");

        javaMailSender.send(message);
    }
}