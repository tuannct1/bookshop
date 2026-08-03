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

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final BlacklistTokenRepository redisTokenRepository;

    public TokenPayload generateAccessToken(CustomUserDetails customUserDetails) {
        //Tạo header và khai báo thuật toán mã hóa HS512
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        String jwtId = UUID.randomUUID().toString();
        //Tạo thời gian khởi tạo
        Date issueTime = new Date();
        //Tạo thời gian hết hạn băgf thời gian khởi tạo + 30p
        Date expiredTime = Date.from(issueTime.toInstant().plus(1, ChronoUnit.MINUTES));
        //Tạo payload
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(jwtId)
                .subject(customUserDetails.getUsername()) // Dùng Email làm thông tin định danh
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                // (Tùy chọn) Thêm Role nếu muốn phân quyền sau này:
                // .claim("roles", user.getRole())
                .build();
        //Chuyển Payload thành kiểu json
        Payload payload = new Payload(claimsSet.toJSONObject());
        //Ghép Header và Payload thành jwt
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            // Dùng secretKey để ký (sign) JWT.
            // Khi ký xong sẽ sinh ra phần Signature.
            // Nếu sau này ai sửa Payload thì chữ ký sẽ không còn hợp lệ.
            jwsObject.sign(new MACSigner(secretKey.getBytes()));
        // Chuyển JWT thành chuỗi String hoàn chỉnh.
        // Kết quả sẽ có dạng:
        // Header.Payload.Signature
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
         // Chuyển chuỗi JWT thành đối tượng SignedJWT
        // để có thể đọc Header, Payload và Signature.
        SignedJWT signedJWT = SignedJWT.parse(token);
        // Lấy thời gian hết hạn (exp) trong Payload của JWT.
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        // Nếu thời gian hết hạn nhỏ hơn thời gian hiện tại
        // thì token đã hết hạn -> trả về false.
        if(expirationTime.before(new Date())){
            return false;
        }
        //Kiểm tra token đã logout chưa
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Optional<BlacklistToken> byId = redisTokenRepository.findById(jwtId);
        if(byId.isPresent()) {
            throw new RuntimeException("Token invalid");
}
        // Kiểm tra chữ ký (Signature) của JWT.
        // Thư viện sẽ dùng secretKey để tính lại Signature.
        // Nếu Signature tính lại giống Signature trong token
        // thì trả về true, ngược lại trả về false.
        return signedJWT.verify(new MACVerifier(secretKey));
    }


    public JwtInfo parseToken(String token) throws ParseException {
        
        // Bước 1: Dịch (parse) chuỗi token (dạng String) thành đối tượng SignedJWT.
        // Mặc định JWT gồm 3 phần: Header.Payload.Signature. 
        // Đối tượng này giúp ta dễ dàng lấy dữ liệu ra khỏi phần Payload.
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        // Bước 2: Đi vào phần Payload (JWTClaimsSet) để lấy ra các thông tin (claims)
        
        // Lấy ra ID duy nhất của Token (thường viết tắt là 'jti' trong chuẩn JWT). 
        // Cái này cực kỳ quan trọng để kiểm tra trong Redis xem token đã bị block (logout) chưa.
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        
        // Lấy ra thời gian mà Token này được sinh ra (thường viết tắt là 'iat' - issued at)
        Date issueTime = signedJWT.getJWTClaimsSet().getIssueTime();
        
        // Lấy ra thời gian mà Token này sẽ hết hạn (thường viết tắt là 'exp' - expiration)
        Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Bước 3: Đóng gói các thông tin rời rạc ở trên vào một đối tượng JwtInfo 
        // Hàm builder() và build() là tính năng của thư viện Lombok giúp tạo Object gọn gàng hơn.
        return JwtInfo.builder()
                .jwtId(jwtId)
                .issueTime(issueTime)
                .expiredTime(expiredTime)
                .build(); // Hoàn tất việc tạo đối tượng và trả về
    }


}   