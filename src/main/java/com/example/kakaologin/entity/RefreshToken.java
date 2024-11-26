package com.example.kakaologin.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id; // Refresh Token은 Redis에 저장하기 때문에 JPA 의존성이 필요하지 않음
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440) // 4시간
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String refreshToken;
    private Long userId;

}