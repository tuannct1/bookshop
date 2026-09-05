package com.example.bookshop.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("OtpCache") 
public class OtpEntity {

    @Id
    private String email; 

    private String otpCode; 

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long expiredTime; 
}