package com.example.kakaologin.service;

import com.example.kakaologin.dto.UserDto;
import com.example.kakaologin.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.kakaologin.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;

    public UserDto kakaoLogin(UserDto dto) {
        User user = userRepository
                        .findByKakaoId(dto.getKakaoId())
                        .orElseGet(() -> userRepository.save(User.from(dto)));
        user.setImgUrl(dto.getImgUrl());
        user.setName(dto.getName());
        return UserDto.from(user);
    }

    public User getLoginUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
    }
}