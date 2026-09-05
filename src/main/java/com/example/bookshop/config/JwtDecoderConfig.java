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

        if (!jwtService.verifyToken(token)) {
            throw new JwtException("Invalid token");
        }

        if (Objects.isNull(nimbusJwtDecoder)) {


            SecretKey secretKeySpec =
                    new SecretKeySpec(
                            secretKey.getBytes(StandardCharsets.UTF_8),
                            "HS512");

            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

    } catch (ParseException | JOSEException e) {

        throw new JwtException("Lỗi giải mã: " + e.getMessage(), e);
    }

    return nimbusJwtDecoder.decode(token);
}
    
} 