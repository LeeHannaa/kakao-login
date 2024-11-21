package com.example.kakaologin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    @GetMapping("/scrd/api/test")
    public String TestApi() {
        System.out.println("test api 요청 호출");
        return "test api success";
    }

    @GetMapping("/scrd/every")
    public String EveryApi() {
        System.out.println("every api 요청 호출");
        return "every api success";
    }
}
