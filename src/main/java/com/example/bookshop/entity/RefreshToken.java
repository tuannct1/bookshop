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
@RedisHash("ValidRefreshToken") 
public class RefreshToken {

    @Id
    private String jwtId;
    
    private String email; 
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiredTime;
}