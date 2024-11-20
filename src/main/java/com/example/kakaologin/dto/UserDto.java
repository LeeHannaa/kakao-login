package com.example.kakaologin.dto;

import com.example.kakaologin.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    private Long id;
    private Long kakaoId;
    private String name;
    private String imgUrl;

    public static UserDto from(User user){
        return UserDto.builder()
                .id(user.getId())
                .kakaoId(user.getKakaoId())
                .name(user.getName())
                .imgUrl(user.getImgUrl())
                .build();
    }
}
