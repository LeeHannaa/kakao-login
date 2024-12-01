package com.example.kakaologin.util;

import com.example.kakaologin.entity.RefreshToken;
import com.example.kakaologin.exception.WrongTokenException;
import com.example.kakaologin.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component // Bean Configuration 파일에 Bean을 따로 등록하지 않아도 사용
public class JwtUtil {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${custom.jwt.expire-time-ms}")
    private long EXPIRE_TIME_MS;
    @Value("${custom.jwt.refresh-expire-time-ms}")
    private long EXPIRE_REFRESH_TIME_MS;


    // JWT Token 발급
    public List<String> createToken(Long userId, String secretKey, long expireTimeMs, long expireRefreshTimeMs) {
        // Claim = Jwt Token중 payload에 들어갈 정보
        // Claim에 userId 넣어 줌으로써 나중에 userId 꺼낼 수 있음
        Claims claims = Jwts.claims();
        claims.put("userId", userId);

        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 정보
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                .compact();

        String refreshToken =  Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 정보
                .setExpiration(new Date(System.currentTimeMillis() + expireRefreshTimeMs)) // set expireRefreshTimeMs
                .signWith(SignatureAlgorithm.HS256, secretKey)   // 사용할 암호화 알고리즘과
                .compact();

        RefreshToken redis = new RefreshToken(refreshToken, userId);
        refreshTokenRepository.save(redis);

        return Arrays.asList(accessToken, refreshToken);
    }
    // Claims에서 memberId 꺼내기
    public static Long getUserId(String token, String secretKey) {
        return extractClaims(token, secretKey).get("userId", Long.class);
    }

    // SecretKey를 사용해 Token Decoding
    private static Claims extractClaims(String token, String secretKey) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new WrongTokenException("만료된 토큰입니다.");
        }
    }

    // 리프레시 토큰 검증 후, 리프레시 + 액세스 토큰 발급
    public List<String> validateRefreshToken(String refreshToken, String secretKey) {
        // 리프레시 토큰 조회 유효하지 않다면 exception 반환 WrongToken
        RefreshToken storedRefreshToken = refreshTokenRepository.findById(refreshToken)
                .orElseThrow(() -> new WrongTokenException("유효하지 않은 리프레시 토큰입니다."));

        // 유효하다면 리프레쉬 토큰 삭제 후 액세스 토큰 발급
        refreshTokenRepository.deleteById(refreshToken);
        Long userId = storedRefreshToken.getUserId();
        System.out.println("새로운 accessToken, refreshToken 발급 시작!!!");

        return this.createToken(userId, secretKey, EXPIRE_TIME_MS, EXPIRE_REFRESH_TIME_MS);

    }
}
