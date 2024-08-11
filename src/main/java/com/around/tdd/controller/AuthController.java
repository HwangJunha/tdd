package com.around.tdd.controller;

import com.around.tdd.service.AuthService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    private final RedisTemplate<String, Object> redisTemplate;

    public AuthController(
            AuthService authService,
            RedisTemplate redisTemplate
    ){
        this.authService = authService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/hello")
    public String helloRedis(){
        redisTemplate.opsForValue().set("test", "test");
        redisTemplate.expire("test", Duration.ofSeconds(30));
        return "hello";
    }




}
