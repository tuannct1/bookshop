package com.example.bookshop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("BlacklistToken")
@Builder
public class BlacklistToken {

    @Id
    private String jwtId;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Long expiredTime;
}