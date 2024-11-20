package com.example.kakaologin.controller;

import com.example.kakaologin.dto.UserDto;
import com.example.kakaologin.dto.response.KakaoLoginResponse;
import com.example.kakaologin.service.AuthService;
import com.example.kakaologin.service.KakaoLoginService;
import com.example.kakaologin.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final KakaoLoginService kakaoLoginService;

    @Value("${custom.jwt.secret}")
    private String SECRET_KEY;

    @Value("${custom.jwt.expire-time-ms}")
    private long EXPIRE_TIME_MS;


    @GetMapping("/scrd/auth/kakao-login")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@RequestParam String code, HttpServletRequest request) {
        UserDto userDto =
                authService.kakaoLogin(
                        kakaoLoginService.kakaoLogin(code,request.getHeader("Referer")+"login/oauth/kakao"));
        System.out.println(userDto);
        return ResponseEntity.ok(
                KakaoLoginResponse.builder()
                        .accessToken(JwtUtil.createToken(userDto.getId(), SECRET_KEY, EXPIRE_TIME_MS))
                        .name(userDto.getName())
                        .imgUrl(userDto.getImgUrl())
                        .build());
    }
}
