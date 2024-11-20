package com.example.kakaologin.entity;

import com.example.kakaologin.dto.UserDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long kakaoId;
    private String name;
    private String email;
    private String tier;
    private String birth;
    private String gender;
    private String imgUrl;

    public static User from(UserDto dto){
        return User.builder()
                .kakaoId(dto.getKakaoId())
                .name(dto.getName())
                .imgUrl(dto.getImgUrl())
                .build();
    }
}
