package com.example.bookshop.service;

import com.example.bookshop.dto.JwtInfo;
import com.example.bookshop.dto.TokenPayload;
import com.example.bookshop.entity.BlacklistToken;
import com.example.bookshop.repository.BlacklistTokenRepository;
import com.example.bookshop.security.CustomUserDetails;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final BlacklistTokenRepository redisTokenRepository;

    public TokenPayload generateAccessToken(CustomUserDetails customUserDetails) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String jwtId = UUID.randomUUID().toString();
        Date issueTime = new Date();
        Date expiredTime = Date.from(issueTime.toInstant().plus(60, ChronoUnit.MINUTES));
        //Tạo payload
        String roles = customUserDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(" "));
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(jwtId)
                .subject(customUserDetails.getUsername())
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                .claim("scope", roles)
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            String token = jwsObject.serialize();
            return TokenPayload.builder()
                .token(token)
                .jwtId(jwtId)
                .expiredTime(expiredTime)
                .build();
        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi khi ký Access Token", e);
        }
    }

    
    public TokenPayload generateRefreshToken(CustomUserDetails customUserDetails ) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String jwtId = UUID.randomUUID().toString();

        Date issueTime = new Date();
        Date expiredTime = Date.from(issueTime.toInstant().plus(7, ChronoUnit.DAYS));

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(jwtId)
                .subject(customUserDetails.getUsername())
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
            String token = jwsObject.serialize();

            return TokenPayload.builder()
                .token(token)
                .jwtId(jwtId)
                .expiredTime(expiredTime)
                .build();

        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi khi ký Refresh Token", e);
        }
    }

    public boolean verifyToken(String token) throws ParseException, JOSEException{
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if(expirationTime.before(new Date())){
            return false;
        }
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Optional<BlacklistToken> byId = redisTokenRepository.findById(jwtId);
        if(byId.isPresent()) {
            throw new RuntimeException("Token invalid");
}
        return signedJWT.verify(new MACVerifier(secretKey));
    }


    public JwtInfo parseToken(String token) throws ParseException {
        
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        
        Date issueTime = signedJWT.getJWTClaimsSet().getIssueTime();
        
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        return JwtInfo.builder()
                .jwtId(jwtId)
                .issueTime(issueTime)
                .expiredTime(expiredTime)
                .build(); 
    }


}   