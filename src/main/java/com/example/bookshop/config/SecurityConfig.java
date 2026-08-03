package com.example.bookshop.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.bookshop.security.CustomUserDetailsService;
import lombok.*;


@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String SECRET_KEY = "DayLaMotChuoiBiMatRatDaiVaAnToan123456";
    private final JwtDecoderConfig decoderConfig;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Dùng công cụ băm một chiều + Salt
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            // TẮT SESSION (Rất quan trọng khi dùng JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize
                // Cấu hình các đường dẫn cho phép truy cập tự do (không cần đăng nhập)
                .requestMatchers("/", "/home", "/api/register", "/api/login", "/css/**", "/js/**", "/error", "/api/refresh").permitAll()
                
                // Cấu hình các đường dẫn bắt buộc phải có quyền ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Tất cả các request còn lại đều bắt buộc phải đăng nhập (ví dụ: vào giỏ hàng, xem đơn)
                .anyRequest().authenticated()
            )
            // Yêu cầu xác minh API bằng JWT và chỉ định công cụ Decoder để xử lý việc giải mã
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(decoderConfig)));

        return http.build();
    }
   

    @Bean
    public AuthenticationManager authenticationManager() {
        // Khởi tạo AuthenticationProvider (chuyên gia xác thực). 
        // Truyền customUserDetailsService vào để chuyên gia biết cách chọc xuống DB lấy thông tin người dùng.
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(customUserDetailsService);
        //Set công cụ so sánh và băm mật khẩu cho AuthenticationProvider để sử dụng
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        // Nhét AuthenticationProvider vào ProviderManager  và trả về.
        return new ProviderManager(authenticationProvider);
}
}
    

