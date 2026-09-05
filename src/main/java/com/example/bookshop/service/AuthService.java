package com.example.bookshop.service;

import com.example.bookshop.dto.JwtInfo;
import com.example.bookshop.dto.TokenPayload;
import com.example.bookshop.dto.request.LoginRequest;
import com.example.bookshop.dto.request.RegisterRequest;
import com.example.bookshop.dto.request.VerifyOtpRequest;
import com.example.bookshop.dto.response.LoginResponse;
import com.example.bookshop.entity.BlacklistToken;
import com.example.bookshop.entity.OtpEntity;
import com.example.bookshop.entity.RefreshToken;
import com.example.bookshop.entity.Role;
import com.example.bookshop.entity.User;
import com.example.bookshop.mapper.UserMapper;
import com.example.bookshop.repository.BlacklistTokenRepository;
import com.example.bookshop.repository.OtpRepository;
import com.example.bookshop.repository.RefreshTokenRepository;
import com.example.bookshop.repository.RoleRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.CustomUserDetails;
import com.example.bookshop.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BlacklistTokenRepository blacklistTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    @Transactional
    public String register(RegisterRequest registerRequest){
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        User user = userMapper.toEntity(registerRequest);

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        
        
        Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy Role mặc định (ROLE_USER)"));
        
        user.setRoles(Collections.singleton(defaultRole));
        
        userRepository.save(user);
        
        String otpCode = generateOTP();
        
        otpRepository.save(OtpEntity.builder()
                .email(user.getEmail())
                .otpCode(otpCode)
                .expiredTime(5L) 
                .build());

        emailService.sendOtpEmail(user.getEmail(), otpCode);
    
    return "Đăng ký thành công. Vui lòng kiểm tra email để nhận mã OTP!";
    }

    public LoginResponse login(LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
        TokenPayload accessPayload = jwtService.generateAccessToken(userDetails);
        TokenPayload refreshPayload = jwtService.generateRefreshToken(userDetails);
        long ttlInMilliseconds = refreshPayload.getExpiredTime().getTime() - System.currentTimeMillis();
        refreshTokenRepository.save(RefreshToken.builder()
            .jwtId(refreshPayload.getJwtId())
            .email(userDetails.getUsername())
            .expiredTime(ttlInMilliseconds)
            .build());
        return LoginResponse.builder()
        .accessToken(accessPayload.getToken())
        .refreshToken(refreshPayload.getToken())
        .build();   
    }
    
    public void logout(String token) throws ParseException {
        JwtInfo jwtInfo = jwtService.parseToken(token);
        String jwtId = jwtInfo.getJwtId();
        Date issueTime = jwtInfo.getIssueTime();
        Date expiredTime = jwtInfo.getExpiredTime();
        if(expiredTime.before(new Date())){
            return;
        }

        BlacklistToken blacklistToken = BlacklistToken.builder()
            .jwtId(jwtId)
            .expiredTime(expiredTime.getTime() - issueTime.getTime())
            .build();
        blacklistTokenRepository.save(blacklistToken);
    }



    public LoginResponse refreshToken(String refreshToken) throws ParseException {
        try {
            if (!jwtService.verifyToken(refreshToken)) {
                throw new RuntimeException("Refresh Token không hợp lệ");
            }

            JwtInfo jwtInfo = jwtService.parseToken(refreshToken);
            String jwtId = jwtInfo.getJwtId();

            RefreshToken storedToken = refreshTokenRepository.findById(jwtId)
                    .orElseThrow(() -> new RuntimeException("Refresh Token không tồn tại hoặc đã bị đăng xuất"));

            String username = storedToken.getEmail();
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

            TokenPayload newAccessPayload = jwtService.generateAccessToken(userDetails);
            TokenPayload newRefreshPayload = jwtService.generateRefreshToken(userDetails);

            refreshTokenRepository.deleteById(jwtId);
            
            long ttlInMilliseconds = newRefreshPayload.getExpiredTime().getTime() - System.currentTimeMillis();
            refreshTokenRepository.save(RefreshToken.builder()
                    .jwtId(newRefreshPayload.getJwtId())
                    .email(username)
                    .expiredTime(ttlInMilliseconds)
                    .build());

            return LoginResponse.builder()
                    .accessToken(newAccessPayload.getToken())
                    .refreshToken(newRefreshPayload.getToken())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi trong quá trình cấp lại Token: " + e.getMessage());
        }
    }


    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); 
        return String.valueOf(otp);
    }

    @Transactional
    public String verifyEmail(VerifyOtpRequest request) {
        OtpEntity otpEntity = otpRepository.findById(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Mã OTP đã hết hạn hoặc email không hợp lệ!"));

        if (!otpEntity.getOtpCode().equals(request.getOtpCode())) {
            throw new IllegalArgumentException("Mã OTP không chính xác!");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Lỗi hệ thống: Không tìm thấy người dùng!"));

        user.setActive(true); 
        userRepository.save(user);

        otpRepository.deleteById(request.getEmail());

        return "Xác thực email thành công! Bạn có thể đăng nhập ngay bây giờ.";
    }
}