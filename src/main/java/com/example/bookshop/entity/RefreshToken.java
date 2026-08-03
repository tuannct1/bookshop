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
@RedisHash("ValidRefreshToken") // Lưu vào một vùng riêng trong Redis
@Builder
public class RefreshToken {

    @Id
    private String jwtId;
    
    private String username; // Nên lưu thêm username để biết token này của ai

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiredTime;
}