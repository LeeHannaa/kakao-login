package com.example.kakaologin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class KakaoLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(KakaoLoginApplication.class, args);
    }

}
