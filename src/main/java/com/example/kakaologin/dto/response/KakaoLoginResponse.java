package com.example.kakaologin.dto.response;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KakaoLoginResponse {
    private String accessToken;
    private String refreshToken;
    private String name;
    private String imgUrl;
}
