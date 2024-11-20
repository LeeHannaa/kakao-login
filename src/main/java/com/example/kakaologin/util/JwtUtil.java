package com.example.kakaologin.util;

import com.example.kakaologin.exception.WrongTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {
    // JWT Token 발급
    public static String createToken(Long userId, String secretKey, long expireTimeMs) {
        // Claim = Jwt Token중 payload에 들어갈 정보
        // Claim에 userId 넣어 줌으로써 나중에 userId 꺼낼 수 있음
        Claims claims = Jwts.claims();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
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
}
