package com.example.bookshop.service;

import com.example.bookshop.dto.JwtInfo;
import com.example.bookshop.dto.TokenPayload;
import com.example.bookshop.dto.request.LoginRequest;
import com.example.bookshop.dto.request.RegisterRequest;
import com.example.bookshop.dto.response.LoginResponse;
import com.example.bookshop.entity.BlacklistToken;
import com.example.bookshop.entity.RefreshToken;
import com.example.bookshop.entity.Role;
import com.example.bookshop.entity.User;
import com.example.bookshop.mapper.UserMapper;
import com.example.bookshop.repository.BlacklistTokenRepository;
import com.example.bookshop.repository.RefreshTokenRepository;
import com.example.bookshop.repository.RoleRepository;
import com.example.bookshop.repository.UserRepository;
import com.example.bookshop.security.CustomUserDetails;
import com.example.bookshop.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

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


    @Transactional
    public String register(RegisterRequest registerRequest){
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại!");
        }

        User user = userMapper.toEntity(registerRequest);

        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        
        
        Role defaultRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Lỗi hệ thống: Không tìm thấy Role mặc định (ROLE_USER)"));
        
        // Sử dụng Collections.singleton vì user đăng ký mới chỉ có đúng 1 role
        user.setRoles(Collections.singleton(defaultRole));
        
        userRepository.save(user);
        
        return "Dang ki thanh cong";
    }
    public LoginResponse login(LoginRequest loginRequest){
        //Khởi tạo authenticationToken để chứa username và password. lí do là vì spring chỉ hiểu UsernamePasswordAuthenticationToken mà không thể hiểu loginrequest
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        //Khởi tạo authenticate để lưu dữ liệu trả về khi xác minh authenticationToken bằng authenticationManager
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //Lấy đổi tượng customUserDetails ở bên trong authenticate gán cho userDetails
        CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
        //Tạo token từ userDetails
        TokenPayload accessPayload = jwtService.generateAccessToken(userDetails);
        TokenPayload refreshPayload = jwtService.generateRefreshToken(userDetails);
        //Lưu refreshToken vào redis
        long ttlInMilliseconds = refreshPayload.getExpiredTime().getTime() - System.currentTimeMillis();
        refreshTokenRepository.save(RefreshToken.builder()
            .jwtId(refreshPayload.getJwtId())
            .username(userDetails.getUsername())
            .expiredTime(ttlInMilliseconds)
            .build());
        //Trả về token cho người dùng
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
            // 1. Kiểm tra Token có hợp lệ, còn hạn và KHÔNG nằm trong Blacklist không
            // Hàm verifyToken của bạn đã bao gồm check trong BlacklistTokenRepository rồi
            if (!jwtService.verifyToken(refreshToken)) {
                throw new RuntimeException("Refresh Token không hợp lệ");
            }

            // 2. Parse token để lấy thông tin cơ bản
            JwtInfo jwtInfo = jwtService.parseToken(refreshToken);
            String jwtId = jwtInfo.getJwtId();

            // 3. Kiểm tra Token có nằm trong danh sách Hợp lệ (Whitelist) không
            RefreshToken storedToken = refreshTokenRepository.findById(jwtId)
                    .orElseThrow(() -> new RuntimeException("Refresh Token không tồn tại hoặc đã bị đăng xuất"));

            // 4. Lấy thông tin UserDetails từ DB dựa vào username đã lưu trong Redis
            String username = storedToken.getUsername();
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

            // 5. Khởi tạo cặp Access Token và Refresh Token MỚI
            TokenPayload newAccessPayload = jwtService.generateAccessToken(userDetails);
            TokenPayload newRefreshPayload = jwtService.generateRefreshToken(userDetails);

            // 6. Xoay vòng (Rotate) Token trên Redis
            // Xóa Refresh Token cũ để nó không thể dùng lại được nữa
            refreshTokenRepository.deleteById(jwtId);
            
            // Lưu Refresh Token mới vào
            long ttlInMilliseconds = newRefreshPayload.getExpiredTime().getTime() - System.currentTimeMillis();
            refreshTokenRepository.save(RefreshToken.builder()
                    .jwtId(newRefreshPayload.getJwtId())
                    .username(username)
                    .expiredTime(ttlInMilliseconds)
                    .build());

            // 7. Trả về kết quả
            return LoginResponse.builder()
                    .accessToken(newAccessPayload.getToken())
                    .refreshToken(newRefreshPayload.getToken())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi trong quá trình cấp lại Token: " + e.getMessage());
        }
    }
}