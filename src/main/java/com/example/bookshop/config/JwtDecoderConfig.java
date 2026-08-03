package com.example.bookshop.config;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.bookshop.service.JwtService;
import com.nimbusds.jose.JOSEException;

import lombok.*;

@Component
@RequiredArgsConstructor
public class JwtDecoderConfig implements JwtDecoder{
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    private final JwtService jwtService; 
    private NimbusJwtDecoder nimbusJwtDecoder = null;
    
    @Override
public Jwt decode(String token) throws JwtException {

    try {

        // Kiểm tra token có hợp lệ hay không.
        // verifyToken() sẽ kiểm tra:
        // 1. Token có hết hạn không.
        // 2. Chữ ký (Signature) có đúng không.
        if (!jwtService.verifyToken(token)) {
            throw new JwtException("Invalid token");
        }

        // Nếu NimbusJwtDecoder chưa được khởi tạo
        // thì mới tạo một lần.
        if (Objects.isNull(nimbusJwtDecoder)) {

            // Chuyển chuỗi secretKey thành SecretKey
            // để NimbusJwtDecoder sử dụng.
            SecretKey secretKeySpec =
                    new SecretKeySpec(
                            secretKey.getBytes(StandardCharsets.UTF_8),
                            "HS512");

            // Tạo JwtDecoder sử dụng thuật toán HS512.
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

    } catch (ParseException | JOSEException e) {

        // Nếu parse hoặc verify token bị lỗi
        // thì chuyển thành JwtException cho Spring Security xử lý.
        throw new JwtException("Lỗi giải mã: " + e.getMessage(), e);
    }

    // Decode JWT thành đối tượng Jwt của Spring Security.
    // Sau đó Spring sẽ đọc các claim như sub, exp, roles...
    return nimbusJwtDecoder.decode(token);
}
    
} 